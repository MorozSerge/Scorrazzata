<#import "parts/common.ftl" as c>

<@c.page>
List of Users
<table class="table">
    <thead class="thead-dark">
    <tr>
        <th scope="col">Name</th>
        <th scope="col">Active</th>
        <th scope="col">Role</th>
        <th scope="col"></th>
    </tr>
    </thead>
    <tbody>
    <#list users as user>
    <tr>
        <th scope="row">${user.username}</th>
        <td><#if user.active>Yes<#else>No</#if></td>
        <td><#list user.roles as role>${role}<#sep>, </#list> </td>
        <td><a href="/user/${user.id}">edit</a> </td>
    </tr>
    </#list>
    </tbody>
</table>
</@c.page>
