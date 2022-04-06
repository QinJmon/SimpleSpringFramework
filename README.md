# spring简易自研框架

## 介绍
本项目模拟Spring框架的IoC容器、依赖注入、AOP、MVC机制，实现了简易版的Spring框架。

## 开发工具
IDEA


## Bean容器的组成部分

1.  保存Class对象及其实例的载体
2.  容器的加载 
3.  容器的操作方式

### 容器的加载 

1. 配置的管理与获取(如何将注解管理起来，随时读取目标注解，得到被它标记的类)
2. 获取指定范围内的Class对象
3. 依据配置提取Class对象,连同实例一并存入容器

### 容器的操作方式

1、增加、删除操作（操作map集合的增删）

2、通过注解来获取被注解标注的Class（即所有的key）

3、根据Class获取对应实例（通过key得到v）

4、通过超类获取对应的子类Class

5、获取所有的Bean实例集合（beanMap.values()）

6、获取容器载体保存Class的数量（map的size）


## 总结实现IoC容器
> 先是定义注解标签（@Component、@Controller、@Service、@Repository），实现了将指定的packageName下的被上述注解标记的class对象和对象相关的实例以键值对的形式保存到
>容器的ConcurrentHashMap类型的成员变量beanMap里，以实现容器的初始化。此外为保证容器实例的唯一性，通过枚举的手段实现了抵御反射和序列化的安全单例。从这里可以看出咱们
>框架管理的Bean都是单例的，并且是非延时加载的（主要是因为实现简单且能满足大多的业务需求）。

## 依赖注入

### 实现逻辑

> 1、遍历Bean容器中所有的Class对象
>
> 2、遍历Class对象的所有成员变量
>
> ```java
> Field[] fields = clazz.getDeclaredFields();
> ```
>
> 3、找出被注解Autowired标记的成员变量（isAnnotationPresent）
>
> 4、获得注解里面的属性和成员变量的类型
>
> 5、根据成员变量的类型获取在容器里对应的实例或者实现类
>
> ```java
> //根据Class在beanContainer里获取其实例或者实现类
> //如果直接根据传入的类对象找到了容器中对应的实例，说明传入的是类。如果在容器中根据class没有得到对应的实例，有两种情况：1、传入的是接口，bean里面存的是实现类  2、就是没有
> 
> Object fieldValue=getFileInstance(fieldClass,autowiredValue);
> //这时就需要定义一个方法来根据接口获得实现类（直接调用上面是实现的容器的操作：通过接口或者父类获取类或者子类的Class集合），获得集合后，如果集合长度大于1且注解属性为空未指定实现类名称就会报错。指定了就去遍历类集合寻找对应class并返回。
> 
> ```
>
> 6、通过反射将对应的成员变量实例注入到成员变量所在类的实例
>
> ```java
> field.set(targetBean,value);
> ```



### DI总结
> 先定义@Autowired注解，实现被该注解依赖注入的逻辑，之后调用doIoC方法去处理已经加载进来的bean实例里面被@Autowired标记的属性，针对这些属性调用getFieldInstance方法去获取这些属性在bean容器里面对应的bean实例，还支持根据接口获取对应的实现类。最后将获取到的实例通过反射注入到成员变量所在类的实例。


## AOP

### 思路：

1、解决标记的问题（识别Aspect以及Advice）,定义横切逻辑的骨架
2、定义Aspect横切逻辑以及被代理方法的执行顺序
3、将横切逻辑织入到被代理的对象以生成动态代理对象

### doAOP实现
> 1、在容器中根据注解获得获取所有的切面类集合
>
> 2、封装AspectInfo类
>
> 将注解Order属性顺序值、通知抽象类对象实例、解析Apect表达式且定位织入目标类的对象实例封装到AspectInfo类里面。
>
> 3、遍历容器里的类
>
> 4、粗筛符合条件的切面类
>
> 5、进行Apect横切逻辑的织入（创建动态代理对象执行）
>
> ```java
> private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
>  if(ValidationUtil.isEmpty(roughMatchedAspectList)){return;}
>  //创建动态代理对象
>  AspectListExecutor aspectListExecutor=new AspectListExecutor(targetClass,roughMatchedAspectList);
>  Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
>  beanContainer.addBean(targetClass,proxyBean);
> }
> ```

## MVC
![输入图片说明](https://images.gitee.com/uploads/images/2022/0205/195952_a0e224ff_9612748.png "屏幕截图.png")

### 大致流程

>获取http请求和需要回发的http响应对象，之后将他们委托给RequestProcessorChain处理。我们只处理get和post方法的请求，RequestProcessorChain参照的是责任链模式的后置处
>理器的处理逻辑，里面保存了处理RequestProcessor接口的多个不同的实现类，之所以会有多个不同的实现类对应为DispatcherServlet是项目里面所有请求的唯一入口。这些请求里即
>会有获取jsp页面的请求，也会有获取静态资源的请求、直接获取json数据的请求等，针对不同的请求会使用不同的RequestProcessor来处理。


### DispatcherServlet实现

继承HttpServlet，重写init和service方法

### init() 初始化

servlet是程序执行的入口：对容器进行初始化并将相关的bean加载进来，同时完成AOP相关逻辑的织入，以及相关的IoC依赖注入操作。同时为了后面能够采用责任链模式实现RequestProcessor矩阵，需要将对应的处理器添加到处理器列表中。

将请求处理器按照PreRequestProcessor、StaticResourceRequestProcessor、JspRequestProcessor、ControllerRequestProcessor进行添加。因为我们的请求经过编码和路径的处理之后再进行后续的处理。将ControllerRequestProcessor放到最后因为它的处理比较耗时，需要将请求和controller的方法实例进行匹配。

### service方法实现
1，创建责任链对象实例
2，通过责任链模式来依次调用请求处理器对请求进行处理
3，对处理结果进行渲染

### Controller请求处理器

##### 功能：

> ◆针对特定请求,选择匹配的Controller方法进行处理
>
> ◆解析请求里的参数及其对应的值,并赋值给Controller方法的参数
>
> ◆选择合适的Render ,为后续请求处理结果的渲染做准备


##### 实现：

**1、依靠容器的能力，建立起请求路径、请求方法与Controller方法实例的映射**

> 1,遍历所有被@RequestMapping修饰的类
>
> 2、获得注解属性也就是一级路径
>
> 3、通过反射获得类中所有方法数组，遍历得到被@RequestMapping修饰的方法
>
> 4、获得注解属性，即二级路径。一级路径和二级路径拼接得到url
>
> 5、解析方法里被@RequestParam标记的参数，将注解属性方法参数名称，参数类型以kv的形式存到map里面。
>
> （我们为了实现简单规定在被注解@RequestMapping标记的方法，如有参数必须用注解@RequestParam标记）
>
> 6、将获取到的信息封装到映射表中



**2、重写请求执行器RequestProcessor 里面的process方法**

> 1、解析HttpServletRequest的请求方法、请求路径，封装为RequestPathInfo，再去映射表中获取对应的ControllerMethod
> 2、解析请求参数，并传递给获取到的controllerMethod 实例去执行
>
> ~~~java
> Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());
> ~~~
>
> 1）从请求里获取get或者post的参数名及其对应的值
>
> 2）获得controllerMethod里面的参数和参数的映射关系，遍历集合，将请求路径中的参数值转为controller方法里面参数所需要的类型。最后将转化后的值添加到方法参数集合中。
>
> 3）利用反射invoke执行controller里面对应的方法并返回结果
>
> 3、根据处理的结果，选择对应的render进行渲染（根据不同的情况设置不同的渲染器）
> 判断方法是否被注解ResponseBody修饰（isAnnotationPresent），是就设置为JsonResultRender，不是就设置为ViewResultRender。