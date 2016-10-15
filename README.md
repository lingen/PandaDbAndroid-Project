# PandaDbAndroid-Project

PandaDbAndroid项目基于Java的 Android开源数据库框架，它的主要特点是：

1. 支持Android 4.0以上
2. 基于Android自带的 SQLite类库实现数据库操作
3. 同步的数据库操作
4. 倡导 SQLite及原生 SQL 编写
5. 封装表的创建及升级
6. 支持自由事务嵌套行为，简化对数据库事务的操作

> 为什么是同步数据库操作

略，此概念已在前面博文中说明

## 如何在项目中引用它

> 源码依赖

从Github中下载源码，找到其中的pandadbandroid子项目，将其复制到你的 Android项目中，做为子项目依赖

> aar依赖
你也可以选择从源码中构建出一个aar类库，将aar复制到你的项目中进行依赖

>> 如果你对Android项目中的aar不理解，请参考此篇博文


## 数据库及表的定义

> 如何一个数据库对象

~~~java
        Table table = TableBuilder.createInstance("user_")
                .textColumn("name_")
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();
~~~

如上所示：

请使用TableBuilder类辅助你构建表

>> 上述代码创建了一个user_表，含name\_,age\_,weight\_,data\_几个定义的字段，由于表未创建主键，系统会自动生成名为OPF\_ID\_的自增主键


>>创建一个自定义主键的表

~~~java
   Table table = TableBuilder.createInstance("user_")
                .primaryColumn("id_", ColumnType.ColumnInt)
                .textColumn("name_")
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();
                
~~~

如上所示，添加了一个主键为id_的列，这种情况下，框架不会为你额外创建其它主键；

> 如里你的表有多个主键，你只需要再添加多个primaryColumn就可以了，框架为你定义成复合主键


>> 创建一个带索引的表

如果你的表，有需要索引的字段，可以创建一个带索引的表

~~~java
        Table table = TableBuilder.createInstance("user_")
                .primaryColumn("id_", ColumnType.ColumnInt)
                .indexColumn("name_",ColumnType.ColumnText,false)
                .intColumn("age_")
                .realColumn("weight_")
                .blobColumn("data_")
                .builder();
                
~~~

如上代码所示，使用indexColumn来创建一个索引列


> 定义一个数据库


~~~java

       //定义表创建
        TableCreate tableCreate = new TableCreate() {
            @Override
            public Table createTable() {
                Table table = TableBuilder.createInstance("user_").textColumn("name_")
                        .intColumn("age_")
                        .realColumn("weight_")
                        .blobColumn("data_")
                        .builder();

                return table;
            }
        };


        List<TableCreate> tableCreates = new ArrayList<>();
        tableCreates.add(tableCreate);

        //定义升级
        TableUpdate tableUpdate = new TableUpdate() {
            @Override
            public String updateTable(int from, int to) {
                if (from == 1 && to == 2){
                    System.out.println("AAA");
                    Log.e("AAA","你调用我了啊");
                }
                return null;
            }
        };

        final Repository repository = Repository.createInstance(appContext,"abc",1, tableCreates,tableUpdate);
        
~~~

如上述代码所示：定义一个Repository对象，这个对象就是你操作数据库的地方
其中：

appContext为你 Android的Context对象

abc为数据库名，1为当前数据库的版本

tableCreates，tableUpdate分别为表创建及表升级

## 数据库 API

### 更新 API

~~~java

//传入一个SQL字符，返回执行结果 是否正确
public boolean executeUpdate(String sql);

//传入一条带参数的SQL语句，返回执行是否成功
public boolean executeUpdate(String sql,SQLParam sqlParam);

~~~

> 示例代码;

~~~java

        //插入测试
        String sql = "insert into user_ (name_,age_,weight_,data_) values (?,?,?,?)";

        SQLParam sqlParam = SQLParam.createInstance()
                .addString("lingen")
                .addString("123")
                .addString("12.12")
                .addString("123");


        boolean success = repository.executeUpdate(sql,sqlParam);

        assertTrue(success);
        
~~~

### 查询API

~~~java
//执行一条查询SQL，不带参数，返回数组
public List<SQLResult> executeQuery(String sql);

//执行一条查询SQL，带参数，返回数组
public List<SQLResult> executeQuery(final String sql,final SQLParam sqlParam);

//执行一条查询SQL，不带参数，期望只返回一条数据
public SQLResult executeSingleQuery(String sql);

//执行一条查询SQL,带参数，期望只返回一条数据
public SQLResult executeSingleQuery(String sql,SQLParam sqlParam);

~~~

> 示例代码：

~~~java

        //查询测试
        List<SQLResult> results = repository.executeQuery("select * from user_");
        assertTrue(results.size() > 0);

        sqlParam = SQLParam.createInstance().addStringArray(new String[]{"lingen1","lingen"});

        List<SQLResult> queryReusts = repository.executeQuery("select * from user_ where name_ in (?)",sqlParam);

        assertTrue(queryReusts.size() > 0);

~~~

> 查询表是否存在

~~~java
public boolean tableExists(String tableName);
~~~

### 自由事务 API

~~~java
//这个API可以把多个数据库操作嵌入一个事务
public boolean executeInTransaction(final TransactionBlock block)
~~~


>示例代码

~~~java

//定义一条插入

    private void insertOne(Repository repository){
        String sql = "insert into user_ (name_,age_,weight_,data_) values (?,?,?,?)";


        SQLParam sqlParam = SQLParam.createInstance()
                .addString("lingen1")
                .addString("123")
                .addString("123.12")
                .addString("123");

        repository.executeUpdate(sql,sqlParam);
    }
    
    //自由事务，包含苦干个插入
    
            //批量插入
        repository.executeInTransaction(new TransactionBlock() {
            @Override
            public boolean execute() {
                long begin = System.currentTimeMillis();
                for (int i =0 ;i<1000;i++){
                    insertOne(repository);
                }
                long end = (System.currentTimeMillis() - begin);
                Log.e("TIME",String.valueOf(end));
                return true;
            }
        });

~~~

如上代码所示，事务是自动识别的，如果有executeInTransaction，则事务在它之内

多个executeInTransaction可重复使用

如果没有executeInTransaction，那executeUpdate等更新API会自动生成一个事务

## 错误SQL自动日志记录机制
对于打包模式下，错误的SQL会自动在用户主目录下存储并记录，开发人员可以随时查看此日志以找到错误的SQL日志

>这个机制还未实现，待完善中

## 主线程自动检测机制
此框架是一个同步数据库操作框架，很容易一不小心会出现在主线程上执行SQL等，这是不允许的，因此框架自行检测这个特性，如果在主线程执行SQL操作，APP就会崩盘

> 这个机制还未实现，待完善中

## 只支持？查询SQL
通常，SQL中带参数，有两种写法
> ? 方式

~~~sql
select * from user where name = ?
~~~

> 命名查询方式

~~~
select * from user where name = :name
~~~

PandaDbAndroid只支持问号这种模式

> 为什么要这样
>> 笔者的框架有一个重要理念：只提供更少的选择
>> 
>> 上述两种方式都可以，但笔者希望APP只使用某一种方式，以保证APP风格的统一；
>> 
>> Android自带的SQLiteOpenHelper只支持？，考虑到集成 C 语言的 SQLite成本太高，因此 Android使用？做参数查询


## 支持的SQL字段及对应

| SQL字段  | Swift字段 |
|----------|----------|
| TEXT     | String |
| INT      | int    |
| Real     | double  |
| Blob     | byte[]    |

对应关系是指，这些字段查询出来的返回值对应Java相应字段

## 对in的处理
C的SQL语法并未提供命名参数in (:?)的原生支持，PandaDbSwift对此做了处理

> SQL写法

~~~
  select * from users where name in (:?)
~~~

> 参数传递

~~~
  new String[]{"lingen","lingen2"}
~~~

> 实现原理：

在遇到in (:name)这种SQL时，会对SQL及参数进行分拆,最终变成

~~~
SQL: select * from uses where name in (?,?)

~~~