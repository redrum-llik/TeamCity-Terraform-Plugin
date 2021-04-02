<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<%@ page import="jetbrains.buildServer.runner.terraform.TerraformCommandType" %>
<%@ page import="jetbrains.buildServer.runner.terraform.TerraformVersionMode" %>

<jsp:useBean id="additionalArgumentsBean" class="jetbrains.buildServer.runner.terraformRunner.beans.AdditionalArgumentsBean"/>
<jsp:useBean id="commandBean" class="jetbrains.buildServer.runner.terraformRunner.beans.CommandBean"/>
<jsp:useBean id="initStageBean" class="jetbrains.buildServer.runner.terraformRunner.beans.InitStageBean"/>
<jsp:useBean id="outputPathBean" class="jetbrains.buildServer.runner.terraformRunner.beans.OutputPathBean"/>
<jsp:useBean id="stateBackupPathBean" class="jetbrains.buildServer.runner.terraformRunner.beans.StateBackupPathBean"/>
<jsp:useBean id="versionBean" class="jetbrains.buildServer.runner.terraformRunner.beans.VersionBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<props:viewWorkingDirectory />

<c:if test="${propertiesBean.properties[versionBean.key] == versionBean.autoDetectModeKey}">
    <div class="parameter">
        ${versionBean.label} ${versionBean.autoDetectModeValue}
    </div>
</c:if>

<c:if test="${propertiesBean.properties[versionBean.key] == versionBean.tfEnvModeKey}">
    <div class="parameter">
        ${versionBean.label} <props:displayValue name="${versionBean.tfEnvKey}" />
    </div>
</c:if>

<c:set var="command" value="${propertiesBean.properties[commandBean.key]}"/>

<c:if test="${not command == commandBean.customKey}">
    <div class="parameter">
        ${commandBean.label} ${TerraformCommandType.valueOf(command).id}
    </div>
</c:if>

<c:if test="${command == commandBean.customKey}">
    <div class="parameter">
            ${commandBean.label} <props:displayValue name="${commandBean.customCommandKey}" />
    </div>
</c:if>

<c:if test="${not empty propertiesBean.properties[outputPathBean.key] && command == commandBean.planKey}">
    <div class="parameter">
        ${outputPathBean.label} <props:displayValue name="${propertiesBean.properties[outputPathBean.key]}" />
    </div>
</c:if>

<c:if test="${not empty propertiesBean.properties[stateBackupPathBean.key] && command == commandBean.applyKey}">
    <div class="parameter">
        ${stateBackupPathBean.label} <props:displayValue name="${propertiesBean.properties[stateBackupPathBean.key]}" />
    </div>
</c:if>


<c:if test="${not empty propertiesBean.properties[additionalArgumentsBean.key]}">
    <div class="parameter">
        ${additionalArgumentsBean.label} <props:displayValue name="${additionalArgumentsBean.key}" />
    </div>
</c:if>

<div class="parameter">
    ${initStageBean.doInitLabel} <props:displayCheckboxValue name="${initStageBean.doInitKey}" checkedValue="Yes" uncheckedValue="No"/>
</div>

<c:if test="${not empty propertiesBean.properties[initStageBean.useWorkspaceKey]}">
    <div class="parameter">
            ${initStageBean.useWorkspaceLabel} <props:displayValue name="${initStageBean.useWorkspaceKey}" />
    </div>
    <div class="parameter">
        ${initStageBean.createWorkspaceIfNotFoundLabel} <props:displayCheckboxValue name="${initStageBean.createWorkspaceIfNotFoundKey}" checkedValue="Yes" uncheckedValue="No"/>
    </div>
</c:if>