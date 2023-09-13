package com.ipharmacare.collect.data.generate;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.JDBCType;
import java.sql.Types;

public class SmallintResolver extends JavaTypeResolverDefaultImpl {
    public SmallintResolver() {
        super.typeMap.put(Types.SMALLINT,
                new JdbcTypeInformation(JDBCType.SMALLINT.name(), new FullyQualifiedJavaType(Integer.class.getName())));
    }
}