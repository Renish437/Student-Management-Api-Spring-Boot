package com.renish.backend.aspect;

import com.renish.backend.annotation.IncludeDeleted;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Hibernate filters (here: the {@code deletedFilter} soft-delete filter) live on the
 * session bound to the <em>current transaction</em>. Toggling the filter outside of a
 * transaction hits a throwaway session and never reaches the queries the repository
 * executes, so this aspect guarantees an active transaction first and only then
 * enables/disables the filter on the session the service call will actually use.
 */
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class DeletedFilterAspect {

    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    @Around("@annotation(includeDeleted)")
    public Object includeDeleted(
            ProceedingJoinPoint joinPoint,
            IncludeDeleted includeDeleted
    ) throws Throwable {

        String filterName = includeDeleted.value();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        Object result = transactionTemplate.execute(status -> {
            Session session = entityManager.unwrap(Session.class);
            boolean wasEnabled = session.getEnabledFilter(filterName) != null;
            session.disableFilter(filterName);
            try {
                return joinPoint.proceed();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable t) {
                thrown.set(t);
                throw new RuntimeException(t);
            } finally {
                // restore the filter state while the session is still open
                if (wasEnabled) {
                    session.enableFilter(filterName);
                } else {
                    session.disableFilter(filterName);
                }
            }
        });

        if (thrown.get() != null) {
            throw thrown.get();
        }
        return result;
    }
}
