<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="bean" class="jetbrains.buildServer.runner.terraformRunner.TerraformBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<c:if test="${propertiesBean.properties[bean.versionKey] eq bean.versionAuto}">
    <div class="parameter">
        Version: <props:displayValue name="Auto-detect" />
    </div>
</c:if>

<c:if test="${propertiesBean.properties[bean.versionKey] eq bean.versionTFEnv}">
    <div class="parameter">
        Version: <props:displayValue name="${bean.versionToFetch}" />
    </div>
</c:if>

    <div class="parameter">
        Command: <props:displayValue name="${propertiesBean.properties[bean.commandKey]}" />
    </div>