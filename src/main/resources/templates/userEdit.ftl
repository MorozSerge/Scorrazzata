<#import "parts/common.ftl" as c>

<@c.page>
User editor

<h5>${user.username}</h5>

<form action="/user" method="post">
    <div>Activation:<input type="checkbox" class="m-1" name ="active" value="true" ${user.active?string("checked", "")}></div>

    <#list roles as role>
    <div>
        <label><input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}>${role}</label>
    </div>
</#list>
<input type="hidden" value="${user.id}" name="userId">
<input type="hidden" value="${_csrf.token}" name="_csrf">
<button type="submit">Save</button>
</form>
</@c.page>