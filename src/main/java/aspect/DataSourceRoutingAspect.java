package aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import routing.DataSourceContextHolder;
import routing.DataSourceType;

@Aspect
@Component
public class DataSourceRoutingAspect {

    @Pointcut("@annotation(transactional)")
    public void transactionalMethods(Transactional transactional) {}

    @Before("transactionalMethods(transactional)")
    public void setDataSource(Transactional transactional) {
        if (transactional.readOnly()) {
            DataSourceContextHolder.set(DataSourceType.READ);
            System.out.println(">>> Routing to READ DB");
        } else {
            DataSourceContextHolder.set(DataSourceType.WRITE);
            System.out.println(">>> Routing to WRITE DB");
        }
    }

    @After("transactionalMethods(transactional)")
    public void clearDataSource(Transactional transactional) {
        DataSourceContextHolder.clear();
    }
}