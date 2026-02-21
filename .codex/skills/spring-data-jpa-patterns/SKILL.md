---
name: spring-data-jpa-patterns
description: |
  Padrões e práticas para Spring Data JPA 1.11.x com Spring Framework 4.3.x. Cobre configuração XML, criação de repositories, query methods automáticos, integração com Services via dependency injection e transações declarativas.

  Use when: Configurar Spring Data JPA em projetos Spring 4.x ou migrar DAOs legados para Repositories com query methods derivados.
version: 1.0.0
tags: [spring-data-jpa, spring-framework, jpa, repository-pattern, java]
---

# Spring Data JPA Patterns (Spring 4.3.x)

## Overview

Padrões de uso de Spring Data JPA 1.11.x com Spring Framework 4.3.x, focando em configuração XML, repositories, query methods e integração com camada de serviços.

## Core Concepts

### Repository Interface Pattern
Spring Data JPA elimina boilerplate de DAOs criando implementações automáticas de interfaces que estendem `JpaRepository`.

**Benefits:**
- CRUD methods prontos (save, findById, findAll, delete, etc.)
- Query methods derivados de nomes de métodos
- Paginação e ordenação built-in
- Integração automática com transações

## Configuration

### XML Configuration (Spring 4.3.x)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/data/jpa
        http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

    <!-- EntityManagerFactory -->
    <bean id="entityManagerFactory"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.example.model"/>
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
                <property name="showSql" value="false"/>
                <property name="generateDdl" value="false"/>
            </bean>
        </property>
    </bean>

    <!-- Transaction Manager -->
    <bean id="transactionManager"
          class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>

    <!-- Enable @Transactional -->
    <tx:annotation-driven transaction-manager="transactionManager"/>

    <!-- Enable Spring Data JPA Repositories -->
    <jpa:repositories base-package="com.example.repository"
                      entity-manager-factory-ref="entityManagerFactory"
                      transaction-manager-ref="transactionManager"/>
</beans>
```

**Key Elements:**
- `<jpa:repositories>`: Scans package for repository interfaces
- `entity-manager-factory-ref`: Links to EntityManagerFactory bean
- `transaction-manager-ref`: Links to transaction manager
- `<tx:annotation-driven>`: Enables @Transactional support

## Repository Patterns

### Basic Repository

```java
package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // CRUD methods inherited from JpaRepository:
    // - save(User entity)
    // - findById(Long id)
    // - findAll()
    // - delete(User entity)
    // - count()
    // - etc.
}
```

### Query Method Derivation

Spring Data JPA generates queries from method names:

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // SELECT * FROM user WHERE name = ?
    List<User> findByName(String name);

    // SELECT * FROM user WHERE email = ?
    User findByEmail(String email);

    // SELECT * FROM user WHERE name LIKE %?%
    List<User> findByNameContaining(String nameFragment);

    // SELECT * FROM user WHERE active = ? AND role = ?
    List<User> findByActiveAndRole(Boolean active, String role);

    // SELECT * FROM user WHERE created_date > ?
    List<User> findByCreatedDateAfter(LocalDate date);

    // SELECT COUNT(*) FROM user WHERE active = ?
    long countByActive(Boolean active);
}
```

**Naming Keywords:**
- `findBy`, `readBy`, `queryBy`, `getBy`
- `countBy`
- `deleteBy`, `removeBy`
- `And`, `Or`
- `Containing`, `Like`, `NotLike`
- `LessThan`, `GreaterThan`, `Between`
- `Before`, `After`
- `IsNull`, `IsNotNull`
- `OrderBy`

### Custom JPQL Queries

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.name = :name")
    List<User> findUsersByName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdDate DESC")
    List<User> findActiveUsersOrderedByDate();

    // Native SQL query
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain", nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);
}
```

## Service Integration

### Injecting Repositories into Services

```java
package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection (recommended)
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<User> searchUsers(String nameFragment) {
        return userRepository.findByNameContaining(nameFragment);
    }

    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByActive(true);
    }

    private void validateUser(User user) {
        // Business validation logic
    }
}
```

### XML Bean Configuration

```xml
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg ref="userRepository"/>
</bean>

<!-- userRepository bean created automatically by <jpa:repositories> -->
```

## Transaction Management

### Declarative Transactions

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // Transaction applied to entire method
    @Transactional
    public Order createOrder(Order order) {
        validateStock(order);
        decrementStock(order);
        return orderRepository.save(order);
    }

    // Read-only optimization
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Rollback on specific exception
    @Transactional(rollbackFor = PaymentException.class)
    public void processPayment(Order order) {
        // Payment logic
    }
}
```

### Transaction Propagation

```java
// REQUIRED (default): Join existing transaction or create new
@Transactional(propagation = Propagation.REQUIRED)
public void method1() { }

// REQUIRES_NEW: Always create new transaction (suspend current)
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void method2() { }

// SUPPORTS: Use transaction if exists, non-transactional otherwise
@Transactional(propagation = Propagation.SUPPORTS)
public void method3() { }
```

## Pagination and Sorting

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByActive(Boolean active, Pageable pageable);
}

// Service usage
public Page<User> getActiveUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size,
        Sort.by("createdDate").descending());
    return userRepository.findByActive(true, pageable);
}
```

## Common Patterns

### Pattern 1: DAO → Repository Migration

**Before (DAO):**
```java
public class UserDAO {
    @PersistenceContext
    private EntityManager em;

    public List<User> findByName(String name) {
        return em.createQuery("SELECT u FROM User u WHERE u.name = :name")
            .setParameter("name", name)
            .getResultList();
    }
}
```

**After (Repository):**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);
}
```

### Pattern 2: Complex Queries with Specifications

For dynamic queries, use Specifications:

```java
import org.springframework.data.jpa.domain.Specification;

public interface UserRepository extends JpaRepository<User, Long>,
                                       JpaSpecificationExecutor<User> {
}

// Usage
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    if (name != null) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    }
    if (active != null) {
        predicates.add(cb.equal(root.get("active"), active));
    }
    return cb.and(predicates.toArray(new Predicate[0]));
};
List<User> results = userRepository.findAll(spec);
```

## Testing Repositories

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByName() {
        User user = new User("John Doe");
        userRepository.save(user);

        List<User> results = userRepository.findByName("John Doe");
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }
}
```

## Best Practices

1. **Use Constructor Injection**
   ```java
   // ✅ Good
   public UserService(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

   // ❌ Avoid field injection
   @Autowired
   private UserRepository userRepository;
   ```

2. **Prefer Query Methods Over @Query**
   ```java
   // ✅ Good - clear, type-safe
   List<User> findByActiveAndNameContaining(Boolean active, String name);

   // ❌ Avoid unless complex
   @Query("SELECT u FROM User u WHERE u.active = ?1 AND u.name LIKE %?2%")
   List<User> customQuery(Boolean active, String name);
   ```

3. **Use @Transactional at Service Layer**
   ```java
   // ✅ Good - Service controls transaction boundaries
   @Service
   @Transactional
   public class UserService { }

   // ❌ Avoid - Repositories are already transactional
   @Transactional
   public interface UserRepository { }
   ```

4. **Mark Read-Only Transactions**
   ```java
   @Transactional(readOnly = true)
   public List<User> findAll() {
       return userRepository.findAll();
   }
   ```

5. **Handle Optional Results**
   ```java
   // ✅ Good
   User user = userRepository.findById(id)
       .orElseThrow(() -> new NotFoundException("User not found"));

   // ❌ Avoid NPE
   User user = userRepository.findById(id).get();
   ```

## Troubleshooting

### No Qualifying Bean Found

**Problem:** `No qualifying bean of type 'UserRepository'`

**Solution:** Ensure `<jpa:repositories>` scans correct package:
```xml
<jpa:repositories base-package="com.example.repository"/>
```

### LazyInitializationException

**Problem:** Accessing lazy collections outside transaction

**Solution:** Use `@Transactional` or fetch joins:
```java
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
User findByIdWithOrders(@Param("id") Long id);
```

### Transactional Method Not Rolling Back

**Problem:** RuntimeException not triggering rollback

**Solution:** Specify rollback conditions:
```java
@Transactional(rollbackFor = Exception.class)
```

## References

- [Spring Data JPA 1.11.x Reference](https://docs.spring.io/spring-data/jpa/docs/1.11.x/reference/html/)
- [Spring Framework 4.3.x Reference](https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/html/)
- [JPA 2.1 Specification](https://jcp.org/en/jsr/detail?id=338)
- Source: `applicationContext.xml`, `src/main/java/br/gov/inep/censo/repository/` (this project)

## Version Compatibility

| Component | Version | Notes |
|-----------|---------|-------|
| Spring Data JPA | 1.11.23.RELEASE | Last version compatible with Spring 4.3.x |
| Spring Framework | 4.3.30.RELEASE | Maintained LTS version |
| JPA | 2.1 | Hibernate 4.2.x compatible |
| Java | 8+ | Lambda support for query specifications |
