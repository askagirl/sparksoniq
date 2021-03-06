# Getting Started

## Prerequisites

### Installing Spark

Sparksoniq requires a spark installation on ideally Linux or Mac.
We recommend installing 2.1.1 from [this page](https://spark.apache.org/downloads.html).
Download the file and unzip it, which will create a directory called spark-2.2.1-bin-hadoop2.7. 

Move over this directory to, for example, /usr/local/bin

    mv spark-2.2.1-bin-hadoop2.7 /usr/local/bin

and add /usr/local/bin/spark-2.2.1-bin-hadoop2.7 to the PATH variable

    export PATH=/usr/local/bin/spark-2.2.1-bin-hadoop2.7/bin:$PATH

You can test that this worked with:

    spark-submit --version

### Installing Sparksoniq

In order to run Sparksoniq, you need to download the .jar file from the [download page](https://github.com/Sparksoniq/sparksoniq/releases)
and put it in a directory of your choice.

### Creating some data set

Create, in the same directory as Sparksoniq, a file data.json and put the following content inside. This is a list of JSON objects in the jsonlines format.

    { "product" : "broiler", "store number" : 1, "quantity" : 20  }
    { "product" : "toaster", "store number" : 2, "quantity" : 100 }
    { "product" : "toaster", "store number" : 2, "quantity" : 50 }
    { "product" : "toaster", "store number" : 3, "quantity" : 50 }
    { "product" : "blender", "store number" : 3, "quantity" : 100 }
    { "product" : "blender", "store number" : 3, "quantity" : 150 }
    { "product" : "socks", "store number" : 1, "quantity" : 500 }
    { "product" : "socks", "store number" : 2, "quantity" : 10 }
    { "product" : "shirt", "store number" : 3, "quantity" : 10 }

## Running simple queries locally

In a shell, from the directory where the sparksoniq .jar lies, type, all on one line:

    spark-submit --class sparksoniq.ShellStart --master local[2] --deploy-mode client
                 jsoniq-spark-app-0.9.1-jar-with-dependencies.jar --master local[2] --result-size 1000
                 
The Sparksoniq shell appears:

    Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
       _____                  __                    ________
      / ___/____  ____ ______/ /___________  ____  /  _/ __ \
      \__ \/ __ \/ __ `/ ___/ //_/ ___/ __ \/ __ \ / // / / /
     ___/ / /_/ / /_/ / /  / ,< (__  ) /_/ / / / // // /_/ /
    /____/ .___/\__,_/_/  /_/|_/____/\____/_/ /_/___/\___\_\
    Master: local[2]
    Item Display Limit: 1000
    Output Path: -
    Log Path: -
    Query Path : -

    jiqs$
    
You can now start typing simple queries like the following few examples. Press *three time* the return key to execute a query.
A warning about the ANTLR version may appear the first time, just ignore it.

    "Hello, World"
 
     1 + 1
 
     (3 * 4) div 5
     
The following query should output the file created above:
     
     json-file("data.json")
     
The above queries do not actually use Spark. Spark is used when the I/O workload can be parallelized, which is the case with a FLWOR expression.
The simplest such query goes like so:

    for $i in json-file("data.json")
    return $i

The above creates a very simple Spark job with only a creation and an action.

Data can be filtered with the where clause. Below the hood, a Spark transformation will be used:

    for $i in json-file("data.json")
    where $i.quantity gt 99
    return $i
    
Sparksoniq also supports grouping and aggregation, like so:

    for $i in json-file("data.json")
    let $quantity := $i.quantity
    group by $product := $i.product
    return { "product" : $product, "total-quantity" : sum($quantity) }
    

Sparksoniq also supports ordering, like so. Note that clauses (where, let, group by, order by) can appear in any order.
The only constraint is that the first clause should be a for clause.

    for $i in json-file("data.json")
    let $quantity := $i.quantity
    group by $product := $i.product
    let $sum := sum($quantity)
    order by $sum descending
    return { "product" : $product, "total-quantity" : $sum }

Finally, Sparksoniq can also send local data to the cluster, exactly like Sparks' parallelize() creation:

    for $i in parallelize((
     { "product" : "broiler", "store number" : 1, "quantity" : 20  },
     { "product" : "toaster", "store number" : 2, "quantity" : 100 },
     { "product" : "toaster", "store number" : 2, "quantity" : 50 },
     { "product" : "toaster", "store number" : 3, "quantity" : 50 },
     { "product" : "blender", "store number" : 3, "quantity" : 100 },
     { "product" : "blender", "store number" : 3, "quantity" : 150 },
     { "product" : "socks", "store number" : 1, "quantity" : 500 },
     { "product" : "socks", "store number" : 2, "quantity" : 10 },
     { "product" : "shirt", "store number" : 3, "quantity" : 10 }
    ))
    let $quantity := $i.quantity
    group by $product := $i.product
    let $sum := sum($quantity)
    order by $sum descending
    return { "product" : $product, "total-quantity" : $sum }

Mind the double parenthesis, as parallelize is a unary function to which we pass a sequence of objects.

## Further steps

More details on FLWOR expressions can be found in the [JSONiq specification](http://www.jsoniq.org/docs/JSONiq/html-single/index.html#chapter-flwor).

