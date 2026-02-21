---
name: hibernate-4-jpa-setup
description: |
  Configuração completa de Hibernate 4.2.x como provedor JPA com Spring Framework 4.3.x. Cobre EntityManagerFactory, transaction management, entity mapping annotations, e integração com Spring Data JPA.

  Use when: Configurar Hibernate 4.2.x em projetos Java 8 com Spring 4.3.x ou manter aplicações legadas compatíveis com JPA 2.1 specification.
version: 1.0.0
tags: [hibernate, jpa, spring-framework, orm, java8, entity-mapping]
---

# Hibernate 4 JPA Setup with Spring 4.3.x

## Overview

Configuração e melhores práticas para Hibernate 4.2.21.Final como provedor JPA em aplicações Spring Framework 4.3.x. Hibernate 4.2.x é a última versão compatível com Java 6-8 e JPA 2.1.

## Dependencies (Maven)

```xml
<properties>
    <hibernate.version>4.2.21.Final</hibernate.version>
    <spring.version>4.3.30.RELEASE</spring.version>
</properties>

<dependencies>
    <!-- Hibernate Core + JPA 2.1 -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>${hibernate.version}</version>
    </dependency>

    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-entitymanager</artifactId>
        <version>${hibernate.version}</version>
    </dependency>

    <!-- Spring ORM -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-orm</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <!-- Database Driver (example: H2) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.3.176</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## Spring Configuration (XML)

### EntityManagerFactory Setup

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- DataSource -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:./data/mydb"/>
        <property name="username" value="sa"/>
        <property name="password" value=""/>
    </bean>

    <!-- JPA Vendor Adapter -->
    <bean id="jpaVendorAdapter"
          class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
        <property name="showSql" value="false"/>
        <property name="generateDdl" value="false"/>
        <property name="database" value="H2"/>
    </bean>

    <!-- EntityManagerFactory -->
    <bean id="entityManagerFactory"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="jpaVendorAdapter" ref="jpaVendorAdapter"/>
        <property name="packagesToScan" value="com.example.model"/>
        <property name="jpaProperties">
            <props>
                <!-- Hibernate Settings -->
                <prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>
                <prop key="hibernate.show_sql">false</prop>
                <prop key="hibernate.format_sql">false</prop>
                <prop key="hibernate.use_sql_comments">false</prop>

                <!-- Performance Optimizations -->
                <prop key="hibernate.jdbc.batch_size">25</prop>
                <prop key="hibernate.order_inserts">true</prop>
                <prop key="hibernate.order_updates">true</prop>

                <!-- Connection Pool (C3P0 example) -->
                <prop key="hibernate.c3p0.min_size">5</prop>
                <prop key="hibernate.c3p0.max_size">20</prop>
                <prop key="hibernate.c3p0.timeout">300</prop>

                <!-- Schema Management -->
                <prop key="hibernate.hbm2ddl.auto">validate</prop>
            </props>
        </property>
    </bean>

    <!-- Transaction Manager -->
    <bean id="transactionManager"
          class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <!-- Enable @Transactional -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

## Entity Mapping

### Basic Entity

```java
package com.example.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    // Constructors
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
```

### Relationship Mapping

**One-to-Many:**
```java
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }
}

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```

**Many-to-Many:**
```java
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

### Enum Mapping

```java
public enum Status {
    ACTIVE(1, "Active"),
    INACTIVE(0, "Inactive");

    private final Integer code;
    private final String label;

    Status(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public Integer getCode() { return code; }
    public String getLabel() { return label; }
}

@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    // Store as ordinal (0, 1, 2...)
    @Enumerated(EnumType.ORDINAL)
    private Status statusOrdinal;

    // Store as string ("ACTIVE", "INACTIVE")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status statusString;

    // Store as custom code (1, 0)
    @Column(name = "status_code")
    private Integer statusCode;

    public Status getStatus() {
        return Stream.of(Status.values())
            .filter(s -> s.getCode().equals(statusCode))
            .findFirst()
            .orElse(null);
    }

    public void setStatus(Status status) {
        this.statusCode = status != null ? status.getCode() : null;
    }
}
```

### Embeddable Types

```java
@Embeddable
public class Address {
    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    // Getters/Setters
}

@Entity
public class Company {
    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip"))
    })
    private Address billingAddress;

    @Embedded
    private Address shippingAddress;
}
```

## Hibernate Properties Reference

### Common Properties

```properties
# Dialect Selection
hibernate.dialect=org.hibernate.dialect.H2Dialect
hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
hibernate.dialect=org.hibernate.dialect.Oracle10gDialect

# Schema Management
hibernate.hbm2ddl.auto=validate   # Validate schema (production)
hibernate.hbm2ddl.auto=update     # Update schema (development)
hibernate.hbm2ddl.auto=create     # Drop and create (testing)
hibernate.hbm2ddl.auto=create-drop # Drop on shutdown (testing)

# SQL Logging
hibernate.show_sql=true           # Log SQL to console
hibernate.format_sql=true         # Format logged SQL
hibernate.use_sql_comments=true   # Add comments to SQL

# Performance
hibernate.jdbc.batch_size=25      # Batch inserts/updates
hibernate.order_inserts=true      # Order inserts for batching
hibernate.order_updates=true      # Order updates for batching
hibernate.jdbc.fetch_size=50      # JDBC fetch size

# Second-Level Cache
hibernate.cache.use_second_level_cache=true
hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory

# Connection Pool (C3P0)
hibernate.c3p0.min_size=5
hibernate.c3p0.max_size=20
hibernate.c3p0.timeout=300
hibernate.c3p0.max_statements=50
```

## EntityManager Usage

### Programmatic EntityManager

```java
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    public void save(User user) {
        entityManager.persist(user);
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public void delete(User user) {
        User managed = entityManager.merge(user);
        entityManager.remove(managed);
    }

    public List<User> findByName(String name) {
        return entityManager
            .createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
            .setParameter("name", name)
            .getResultList();
    }
}
```

### Criteria API (JPA 2.1)

```java
import javax.persistence.criteria.*;

public List<User> searchUsers(String nameFilter, Boolean active) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<User> root = query.from(User.class);

    List<Predicate> predicates = new ArrayList<>();

    if (nameFilter != null) {
        predicates.add(cb.like(root.get("name"), "%" + nameFilter + "%"));
    }

    if (active != null) {
        predicates.add(cb.equal(root.get("active"), active));
    }

    query.where(cb.and(predicates.toArray(new Predicate[0])));
    query.orderBy(cb.asc(root.get("name")));

    return entityManager.createQuery(query).getResultList();
}
```

## Best Practices

### 1. Use Lazy Loading by Default

```java
// ✅ Good - lazy loading
@ManyToOne(fetch = FetchType.LAZY)
private Department department;

// ❌ Avoid - eager loading causes N+1 queries
@ManyToOne(fetch = FetchType.EAGER)
private Department department;
```

### 2. Use Fetch Joins for Collections

```java
// ✅ Good - fetch join avoids N+1
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
Department findByIdWithEmployees(@Param("id") Long id);

// ❌ Avoid - causes N+1 queries
Department dept = findById(id);
dept.getEmployees().size(); // Triggers separate query per employee
```

### 3. Implement equals() and hashCode()

```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

### 4. Use @Transient for Non-Persisted Fields

```java
@Entity
public class User {
    @Column(name = "password_hash")
    private String passwordHash;

    @Transient
    private String plainPassword; // Not persisted

    @Transient
    private Map<String, Object> metadata; // Not persisted
}
```

### 5. Batch Operations

```java
public void batchInsert(List<User> users) {
    int batchSize = 25;
    for (int i = 0; i < users.size(); i++) {
        entityManager.persist(users.get(i));
        if (i % batchSize == 0 && i > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

## Troubleshooting

### LazyInitializationException

**Problem:** Accessing lazy collection outside transaction

**Solution:**
```java
// Option 1: Use fetch join
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")

// Option 2: Use @Transactional on service method
@Transactional
public User getUserWithOrders(Long id) {
    User user = userRepository.findById(id).get();
    user.getOrders().size(); // Initialize within transaction
    return user;
}
```

### MultipleBagFetchException

**Problem:** Cannot fetch multiple collections with `FetchType.EAGER`

**Solution:**
```java
// ❌ Avoid - causes exception
@OneToMany(fetch = FetchType.EAGER)
private List<Order> orders;

@OneToMany(fetch = FetchType.EAGER)
private List<Address> addresses;

// ✅ Use lazy + fetch joins
@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders;

@OneToMany(fetch = FetchType.LAZY)
private List<Address> addresses;

@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders LEFT JOIN FETCH u.addresses WHERE u.id = :id")
```

### Schema Validation Errors

**Problem:** `Table "user" not found`

**Solution:** Verify `hibernate.hbm2ddl.auto` setting:
```xml
<prop key="hibernate.hbm2ddl.auto">validate</prop>
<!-- OR run schema SQL manually -->
```

## References

- [Hibernate 4.2 Documentation](https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html/)
- [JPA 2.1 Specification](https://jcp.org/en/jsr/detail?id=338)
- [Spring ORM Documentation](https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/html/orm.html)
- Source: `applicationContext.xml`, `src/main/java/br/gov/inep/censo/model/` (this project)

## Version Compatibility

| Component | Version | Notes |
|-----------|---------|-------|
| Hibernate | 4.2.21.Final | Last Java 6-8 compatible version |
| JPA | 2.1 | Criteria API, Schema Generation |
| Spring ORM | 4.3.30.RELEASE | Hibernate 4.2.x support |
| Java | 6-8 | Hibernate 5+ requires Java 8+ |
