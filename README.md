# 基于JSON配置的表单字段验证引擎

## 功能介绍

这是一个基于JSON配置的表单字段验证引擎，支持复杂逻辑的表单验证，包括：
- 基本字段验证（必填、格式、长度等）
- 条件判断验证（满足特定条件时才执行验证）
- 跨字段验证（比较两个字段的值）
- 国际化错误信息支持
- 高性能的验证执行

## 技术栈

- Java 1.8
- Spring Boot 2.7.18
- Fastjson 1.2.83
- JUnit 4

## 核心组件

### 1. ValidationRule
验证规则类，定义表单字段的验证规则，包括：
- fieldName: 字段名称
- validatorType: 验证器类型
- validatorParams: 验证器参数
- errorMessageKey: 错误提示信息的国际化键
- condition: 条件判断，满足条件时才执行该验证规则
- crossFieldValidation: 是否为跨字段验证
- relatedFieldName: 关联字段名称，用于跨字段验证

### 2. Condition
条件判断类，定义验证规则的执行条件，包括：
- conditionType: 条件类型（equals、notEquals、greaterThan、lessThan等）
- fieldName: 字段名称
- compareValue: 比较值
- relatedFieldName: 关联字段名称，用于跨字段条件判断
- crossFieldCondition: 是否为跨字段条件判断

### 3. Validator
验证器接口，所有具体的验证器都需要实现这个接口，包括：
- validate(): 执行具体的验证逻辑
- getDefaultErrorMessage(): 获取默认的错误提示信息
- getValidatorType(): 获取验证器类型

### 4. ValidationEngine
验证引擎核心类，负责解析和执行验证规则，包括：
- parseRules(): 解析JSON格式的验证规则
- validate(): 执行验证
- registerValidator(): 注册自定义验证器

## 内置验证器

目前内置了一个样例验证器：
- RequiredValidator: 必填验证器，验证字段是否必填

## 使用示例

### 1. 配置验证规则

```json
[
  {
    "fieldName": "username",
    "validatorType": "required",
    "errorMessageKey": "username.required",
    "condition": {
      "conditionType": "equals",
      "fieldName": "isRegister",
      "compareValue": true
    }
  }
]
```

### 2. 调用验证引擎

```java
// 解析验证规则
JSONArray rulesJson = JSONArray.parseArray(rulesStr);
List<ValidationRule> rules = validationEngine.parseRules(rulesJson);

// 创建表单数据
JSONObject formData = new JSONObject();
formData.put("isRegister", true);

// 执行验证
ValidationResult result = validationEngine.validate(formData, rules, Locale.CHINA);

// 处理验证结果
if (!result.isValid()) {
    List<ValidationError> errors = result.getErrors();
    // 处理错误信息
}
```

### 3. REST API调用

```bash
POST /api/validation/validate
Content-Type: application/json

{
  "rules": [
    {
      "fieldName": "username",
      "validatorType": "required",
      "errorMessageKey": "username.required"
    }
  ],
  "formData": {
    "email": "test@example.com"
  },
  "locale": "zh_CN"
}
```

## 扩展自定义验证器

1. 实现Validator接口：

```java
public class CustomValidator implements Validator {
    @Override
    public boolean validate(Object fieldValue, JSONObject params, JSONObject formData) {
        // 自定义验证逻辑
    }

    @Override
    public String getDefaultErrorMessage(Locale locale) {
        // 自定义错误提示信息
    }

    @Override
    public String getValidatorType() {
        return "custom"; // 验证器类型
    }
}
```

2. 注册验证器：

```java
validationEngine.registerValidator(new CustomValidator());
```

## 性能优化

- 使用ConcurrentHashMap存储验证器，提高验证器查找效率
- 验证规则解析一次，可多次使用
- 条件判断提前过滤不需要验证的规则
- 验证逻辑尽可能简洁高效

## 国际化支持

验证引擎支持国际化错误信息，通过Spring的MessageSource实现。配置方法：

1. 在Spring配置文件中配置MessageSource：

```xml
<bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
    <property name="basename" value="messages" />
</bean>
```

2. 创建国际化资源文件：
- messages.properties（默认）
- messages_en_US.properties（英文）
- messages_zh_CN.properties（中文）

3. 在验证规则中指定错误信息的国际化键：

```json
{
  "fieldName": "username",
  "validatorType": "required",
  "errorMessageKey": "username.required"
}
```

## 测试

项目包含JUnit测试用例，运行测试：

```bash
mvn test
```

## 总结

这个验证引擎提供了一个灵活、可扩展的表单验证解决方案，支持复杂的验证逻辑和国际化需求。通过JSON配置验证规则，使得验证逻辑与业务代码分离，提高了代码的可维护性和复用性。