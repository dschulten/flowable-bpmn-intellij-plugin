package com.valb3r.bpmn.intellij.plugin.bpmn.api.info

import com.valb3r.bpmn.intellij.plugin.bpmn.api.info.PropertyValueType.BOOLEAN
import com.valb3r.bpmn.intellij.plugin.bpmn.api.info.PropertyValueType.STRING

enum class PropertyType(
        val id: String,
        val caption: String,
        val valueType: PropertyValueType,
        val path: String = id,
        val cascades: Boolean = false,
        val updatedBy: PropertyType? = null,
        val updateOrder: Int = 0,
        val elementUpdateChangesClass: Boolean = false
) {
    ID("id", "ID", STRING, "id.id", true, null, 1000), // ID should fire last
    NAME("name", "Name", STRING),
    DOCUMENTATION("documentation", "Documentation", STRING),
    IS_FOR_COMPENSATION("forCompensation", "Is for compensation", BOOLEAN),
    ASYNC("async", "Asynchronous", BOOLEAN),
    ASSIGNEE("assignee", "Assignee", STRING),
    CALLED_ELEM("calledElement", "Called element", PropertyValueType.EXPRESSION),
    CALLED_ELEM_TYPE("calledElementType", "Called element type", STRING),
    INHERIT_VARS("inheritVariables", "Inherit parent variables", BOOLEAN),
    FALLBACK_TO_DEF_TENANT("fallbackToDefaultTenant", "Fallback to default tenant", BOOLEAN),
    EXCLUSIVE("exclusive", "Exclusive", BOOLEAN),
    EXPRESSION("expression", "Expression", PropertyValueType.EXPRESSION),
    DELEGATE_EXPRESSION("delegateExpression", "Delegate expression", PropertyValueType.EXPRESSION),
    CLASS("clazz", "Class", PropertyValueType.CLASS),
    SKIP_EXPRESSION("skipExpression", "Skip expression", PropertyValueType.EXPRESSION),
    IS_TRIGGERABLE("triggerable", "Is activity triggerable?", BOOLEAN),
    DUE_DATE("dueDate", "Due date", STRING),
    CATEGORY("category", "Category", STRING),
    FORM_KEY("formKey", "Form key", STRING),
    FORM_FIELD_VALIDATION("formFieldValidation", "Form field validation", BOOLEAN),
    PRIORITY("priority", "Priority", STRING),
    SCRIPT("scriptBody", "Script body", STRING),
    SCRIPT_FORMAT("scriptFormat", "Script format", STRING),
    AUTO_STORE_VARIABLES("autoStoreVariables", "Auto store variables", BOOLEAN),
    RULE_VARIABLES_INPUT("ruleVariablesInput", "Rule variables input", STRING),
    RULES("rules", "Rules", STRING),
    RESULT_VARIABLE("resultVariable", "Result variable", STRING),
    RESULT_VARIABLE_NAME("resultVariableName", "Result variable name", STRING),
    EXCLUDE("exclude", "Exclude", BOOLEAN),
    SOURCE_REF("sourceRef","Source reference", STRING, "sourceRef", false, ID),
    TARGET_REF("targetRef", "Target reference", STRING, "targetRef", false, ID),
    ATTACHED_TO_REF("attachedToRef", "Attached to", STRING, "attachedToRef.id", false, ID),
    CONDITION_EXPR_VALUE("conditionExpression.text", "Condition expression", PropertyValueType.EXPRESSION, "conditionExpression.text"),
    CONDITION_EXPR_TYPE("conditionExpression.type", "Condition expression type", STRING, "conditionExpression.type"),
    COMPLETION_CONDITION("completionCondition.condition", "Completion condition", PropertyValueType.EXPRESSION, "completionCondition.condition"),
    DEFAULT_FLOW("defaultElement", "Default flow element", PropertyValueType.ATTACHED_SEQUENCE_SELECT, "defaultElement", false, ID),
    IS_TRANSACTIONAL_SUBPROCESS("transactionalSubprocess", "Is transactional subprocess", BOOLEAN, "transactionalSubprocess", elementUpdateChangesClass = true),
    IS_USE_LOCAL_SCOPE_FOR_RESULT_VARIABLE("useLocalScopeForResultVariable", "Use local scope for result varaible", BOOLEAN),
    CAMEL_CONTEXT("camelContext", "Camel context", STRING),
    DECISION_TABLE_REFERENCE_KEY("decisionTableReferenceKey", "Decision table reference key", STRING),
    DECISION_TASK_THROW_ERROR_ON_NO_HITS("decisionTaskThrowErrorOnNoHits", "Throw error if no rule hit", BOOLEAN),
    FALLBACK_TO_DEF_TENANT_CDATA("fallbackToDefaultTenantCdata", "Fallback to default tenant", BOOLEAN),
    REQUEST_METHOD("requestMethod", "Request method", STRING),
    REQUEST_URL("requestUrl", "Request URL", STRING),
    REQUEST_HEADERS("requestHeaders", "Request headers", STRING),
    REQUEST_BODY("requestBody", "Request body", STRING),
    REQUEST_BODY_ENCODING("requestBodyEncoding", "Request encoding", STRING),
    REQUEST_TIMEOUT("requestTimeout", "Request timeout", STRING),
    DISALLOW_REDIRECTS("disallowRedirects", "Disallow redirects", BOOLEAN),
    FAIL_STATUS_CODES("failStatusCodes", "Fail status codes", STRING),
    HANDLE_STATUS_CODES("handleStatusCodes", "Handle status codes", STRING),
    RESPONSE_VARIABLE_NAME("responseVariableName", "Response variable name", STRING),
    IGNORE_EXCEPTION("ignoreException", "Ignore exception", STRING),
    SAVE_REQUEST_VARIABLES("saveRequestVariables", "Save request variables to", STRING),
    SAVE_RESPONSE_PARAMETERS("saveResponseParameters", "Save response,status,headers to", STRING),
    RESULT_VARIABLE_PREFIX("resultVariablePrefix", "Result variable prefix", STRING),
    SAVE_RESPONSE_PARAMETERS_TRANSIENT("saveResponseParametersTransient", "Save response as transient variable", STRING),
    SAVE_RESPONSE_VARIABLE_AS_JSON("saveResponseVariableAsJson", "Save response as json", STRING),
    HEADERS("headers", "Headers", STRING),
    TO("to", "To", STRING),
    FROM("from", "From", STRING),
    SUBJECT("subject", "Subject", STRING),
    CC("cc", "CC", STRING),
    BCC("bcc", "BCC", STRING),
    TEXT("text", "Text", STRING),
    HTML("html", "Html", STRING),
    CHARSET("charset", "Charset", STRING)
}
