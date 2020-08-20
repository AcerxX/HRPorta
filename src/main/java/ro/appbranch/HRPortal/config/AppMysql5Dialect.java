package ro.appbranch.HRPortal.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StringType;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppMysql5Dialect extends MySQL5Dialect {

    public AppMysql5Dialect() {
        super();
        this.registerFunction("GROUP_CONCAT", new StandardSQLFunction("GROUP_CONCAT", new StringType()));
    }
}
