# 利用Apache HttpClient 写一个校园学生身份认证程序
(其实就是模拟客户端登陆学校信息门户获取学生信息)

在开发的小程序里，为了安全考虑，打算做一个校园认证：通过利用学生的账号密码，获取学生的专业、班级、姓名三项信息输入到数据库（只获取这些信息，而且这些信息不公开）。

这里要用到的external jar有apache-httpclient-4.0.3.jar和apache-httpcore.jar [下载](http://hc.apache.org/)

代码里注释我写得挺详细的，如果有必要我会补充的。至于如何获得login地址，自己抓包分析不难得到的。
*****
#### 因为我们学校的系统登陆不需要验证码，所以这里的登陆例子没有输入验证码的过程，我在这里 ***给需要验证码登陆的同学提供一下思路：***  
需要用验证码登陆的时候，后端系统是根据获取验证码时你的cookie和登陆时的cookie是否一致来判断你的验证码是否对应和正确的，所以get验证码所用的Client和post登陆时用的Client一致就可以通过验证码登陆了(Client里包括了你的cookie等信息)。
