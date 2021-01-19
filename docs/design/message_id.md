## 消息 ID 生成方案 (YeinGid)

为什么不选择现有流行的分布 ID 算法：

* Snowflake
* UUID

Snowflake 算法使用一个 64bits 的整型数据，非常广泛的应用在各种业务系统里。根据当前的时间来生成 ID。**对于存储空间占用非常少**，但是目前主流的实现像百度的 [uid-generator](https://github.com/baidu/uid-generator)
与美团的 [Leaf](https://github.com/Meituan-Dianping/Leaf) 都需要中央控制端来生成 ID。使用与 UUID 相比更加的复杂，同时提高业务架构及维护的复杂程度。

UUID(Universally unique identifier) 是一种软件建构的标准，亦为自由软件基金会组织在分散式计算环境领域的一部份。UUID 的目的，是让分散式系统中的所有元素，都能有唯一的辨识信息，
而不需要通过中央控制端来做辨识信息的指定。**使用最为简单**，但 UUID 长度为 36 字符占用存储空间较大，是 Snowflake 的 4 倍。

YeinGid 设计的目标：
* 没有中央控制端依赖（部署简单）
* 固定长度（存储时占用空间更少）
* 趋势递增
* 比 UUID 占用更少的存储空间

> YeinGid 长度为 70bits **固定**为 14bytes 的全局唯一字符串，无需中央控制端，相比 UUID 少占用 60% 的存储空间。

### 算法

| version | timestamp | sequence | fid    |
|---------|-----------|----------|--------|
| 3bits   | 32bits    | 18bits   | 17bits |

在指定机器 & 同一时刻 & 某一并发序列中是唯一的。据些生成 **70bits** 的全局唯一 ID。默认采用上表格字节分配方式：

* version(3bits)

  YeinGid 算法的版本号，当前固定取值为： `1`。

* timestamp(32bits)

  当前时间戳，单位：秒，可支持到 **2089** 年。

* sequence(18bits)

  第秒下的并发序列，18bits 可支持每秒 **262143** 并发。

* fid(17bits)

  标识 ID，用于区分在同一时间不同并发序列的标识，最多同时支持 **131071** 个并发序列。

### 编码

YeinGid 采用扩展的 **32 进制**编码。每 **5bits** 编码为 **1** 字节， **70bits** 将编码为 **14** 字节。
<pre>
                      表格：32 进制编码字母表

Value Encoding  Value Encoding  Value Encoding  Value Encoding
    0 2             8 C            16 K            24 S
    1 3             9 D            17 L            25 T
    2 4            10 E            18 M            26 U
    3 5            11 F            19 N            27 V
    4 6            12 G            20 O            28 W
    5 7            13 H            21 P            29 X
    6 A            14 I            22 Q            30 Y
    7 B            15 J            23 R            31 Z
</pre>

### 示例

生成

```
var gidStr = new YeinGid(1234).toHexString();
```

将返回一个像 `7K2GNILFYN27DS` 的全局 ID。

解析

```
YeinGid gid = YeinGid.fromString("7K2GNILFYN27DS");
System.out.println("version: " + gid.getVersion());
System.out.println("timestamp: " + gid.getTimestamp());
System.out.println("seq: " + gid.getSeq());
System.out.println("fid: " + gid.getFid());
```

以上代码输出结果为：
> version: 1
>
> timestamp: 1611025873
>
> seq: 97944
>
> fid: 5432
