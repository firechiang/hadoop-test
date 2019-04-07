package com.firecode.hadooptest.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

/**
 * Hive自定义函数
 * 
 * 1，将当前代码打包
 * 2，用户Hive客户端连接上Hiveserver2服务
 * 3，执行命令：add jar /home/hive-function/TuoMin.jar;            # /home/hive-function/tuoMin.jar是当前代码所打成的jar包所在服务器目录地址
 * 4，执行命令：create temporary function tuo_min as 'com.firecode.hadooptest.hive.udf.TuoMin';  # 创建名字叫tuo_min的函数，
 *    temporary表示是临时函数，用户退出之后该函数就没有了
 * @author JIANG
 */
public class TuoMin extends GenericUDF {

	/**
	 * 在evaluate()方法之前调用。该方法接受的参数是一个ObjectInspectors数组。
	 * 该方法明确 evaluate 方法返回值类型（可以根据参数的个数或类型不同，来明确 evaluate 函数的返回值类型）
	 */
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		
		
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

	/**
	 * 这个方法类似UDF的evaluate()方法。
	 * 它处理真实的参数，并返回最终结果。
	 */
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		if(arguments != null && arguments.length != 0) {
			DeferredObject val = arguments[0];
			String value = "***"+val.get().toString()+"***";
			Text text = new Text();
			text.set(value);
			return text;
		}
		return "空值";
	}

	/**
	 * 这个方法用于当实现的GenericUDF出错的时候，打印出提示信息。
	 * 而提示信息就是你实现该方法最后返回的字符串。
	 */
	@Override
	public String getDisplayString(String[] children) {
        /*assert (children.length == 2);
        return "array_contains(" + children[0] + ", " + children[1] + ")";*/
		return "出错了，长度是:"+children.length;
	}

}
