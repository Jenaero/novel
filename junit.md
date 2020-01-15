##Junit4与Junit5比较

###用法说明
[Junit4与Junit5比较](https://blog.csdn.net/kmesky/article/details/102984592) 
####Spring Boot 2.2 之前的测试类
```java
package com.example.demo1;
    
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo1ApplicationTests {

   @Test
   public void contextLoads() {
   }
}
```

####Spring Boot 2.2 之后的测试类
```java
package com.example.demo;
 
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
 
@SpringBootTest
class DemoApplicationTests {
 
    @Test
    void contextLoads() {
    }
} 
```
####Spring Boot 2.2 之前的 pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>2.1.10.RELEASE</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
####Spring Boot 2.2 之后的 pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>

```
####测试Spring Boot应用程序
>* Spring Boot提供了一个@SpringBootTest注释，spring-test @ContextConfiguration当您需要Spring Boot功能时，它可以用作标准注释的替代。注释通过创建ApplicationContext在测试中使用过的来SpringApplication起作用。除了@SpringBootTest提供许多其他注释外，还用于测试应用程序的更多特定部分。
>如果使用的是JUnit 4，请不要忘记也将其添加@RunWith(SpringRunner.class)到测试中，否则注释将被忽略。如果您使用的是JUnit 5，则无需添加等价项@ExtendWith(SpringExtension.class)，@SpringBootTest并且其他@…Test注释已经在其中进行了注释。
