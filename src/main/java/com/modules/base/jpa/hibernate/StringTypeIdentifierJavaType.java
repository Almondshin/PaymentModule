package com.modules.base.jpa.hibernate;

import com.modules.base.domain.StringTypeIdentifier;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * - 생성자: 클래스의 타입을 받아, 이 타입의 객체를 처리하도록 설정합니다.
 * - 불변성 계획: `getMutabilityPlan` 메서드는 객체가 불변임을 Hibernate에 알립니다.
 * - JDBC 타입 추천: `getRecommendedJdbcType` 메서드는 이 클래스의 값들이 데이터베이스에서 `VARCHAR` 타입으로 저장되어야 함을 Hibernate에 알립니다.
 * - 문자열 변환: `toString`과 `fromString` 메서드는 `StringTypeIdentifier` 객체와 문자열 간의 변환을 처리합니다.
 * - 랩핑 및 언랩핑: `wrap`과 `unwrap` 메서드는 Hibernate가 내부적으로 값을 래핑하거나 언랩핑할 때 호출됩니다.
 * */
public abstract class StringTypeIdentifierJavaType<T extends StringTypeIdentifier> implements UserType {

    private final Class<T> clazz;

    protected StringTypeIdentifierJavaType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class<T> returnedClass() {
        return clazz;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public T nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String value = (String) StringType.INSTANCE.nullSafeGet(rs, names, session, owner);
        if (value == null) {
            return null;
        }
        try {
            return clazz.getDeclaredConstructor(String.class).newInstance(value);
        } catch (Exception ex) {
            throw new HibernateException("Failed to instantiate " + clazz.getName(), ex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            StringType.INSTANCE.nullSafeSet(st, null, index, session);
        } else {
            StringType.INSTANCE.nullSafeSet(st, ((StringTypeIdentifier) value).stringValue(), index, session);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
