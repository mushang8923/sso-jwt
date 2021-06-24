代码办法分为main和gatewayfeature
main分支上实现，是将业务系统作为资源服务器验证token的
gatewayfeature是以网关为资源服务器统一验证token然后转发到后端api，这个分支的逻辑目前来说最符合我们的业务需求

背景：
公司内部的业务系统，多而繁杂，每个系统都有一套单独登录鉴权级制，为避免重复开发和提升用户体验，需要对已有的系统做单点登录控制。
看下业务交互:



1.访问资源接口：前端判断是否带token。
2.没有token，调用CRM代理的授权接口并带上系统标识（区分客户端用）
3.CRM代理的授权接口进行转发到uaa前端的授权页
4.uaa前端授权页调用uaa的代理授权接口。
5.uaa后端验证未登录，重定向到uaa登录页并带上系统标识。
6.输入账号和密码。
7.调用登录接口。
8.登录成功以后，由前端调用uaa的代理授权接口并带上系统标识。
9.授权成功后跳转到crm的授权码页面。
10.前端在授权码页拿到code，调用crm的代理获取token接口（crm系统的代理获取token接口直接调uaa的token接口）。
11.返回token，crm前端拿token访问资源。


已登录系统2.png

1.访问资源接口：前端判断是否带token。
2.没有token，调用CRM代理的授权接口并带上系统标识（区分客户端用）。
3.CRM代理的授权接口进行转发到uaa前端的授权页。
4.uaa前端授权页调用uaa的代理授权接口。
5.uaa后端验证已登录，跳转到cms的授权码页面。
6.前端在授权码页拿到code，调用crm的代理获取token接口（crm系统的代理获取token接口直接调uaa的token接口）
7.返回token，crm前端拿token访问资源。


刷新token.png

filter组成.png

从调用上来看，针对不同的请求，security-oauth2对应的filter是不一样的。代码入口FilterChainProxy.doFilterInternal.
获取token、token_key等接口时会把Client_id和Client_secret做为身份信息去调用，其他的接口则必须走用户校验，所以两者是有区别的。
所以需要保证，授权的时候，session必须一致，这样才能拿到登录了的身份信息，所以流程里面只要是授权都要调到uaa前端统一去调用，以保证uaa后端能够根据sessionId拿到已登录的身份信息。
到此为止，统一登录的问题已解决。
接下来是解决统一登出的问题：
我采用的方案是：
1.登录成功后，将login:user_id作为key，时间戳作为值放入缓存中，在获取token的时候，从缓存中拿到值，放到jwt中。
2.资源服务器在解析token的时候，拿到token的附加信息loginVersion,然后将loginVersion与缓存中的值比较，不一致说明，已退出登录。
3.推出登录时将缓存的信息移除
