package pmmlParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmg.pmml.FieldName;

/**
 * 使用模型
 * @author liaotuo
 *
 */
public class ModelCalc {
	public static void main(String[] args) throws IOException {
		if(args.length < 2){
			System.out.println("参数个数不匹配");
		}
		 //文件生成路径   
        PrintStream ps=new PrintStream("D:\\result.txt");   
        System.setOut(ps);   
        
		String pmmlPath = args[0];  
		String modelArgsFilePath = args[1];
		
		ModelInvoker invoker = new ModelInvoker(pmmlPath);
		 List<Map<FieldName, Object>> paramList = readInParams(modelArgsFilePath);
		 int lineNum = 0;  //当前处理行数
		 for(Map<FieldName, Object> param : paramList){
			 lineNum++;
			 System.out.println("======当前行： " + lineNum + "=======");
			 Map<FieldName, ?> result = invoker.invoke(param);
			 Set<FieldName> keySet = result.keySet();  //获取结果的keySet
			 for(FieldName fn : keySet){
				 System.out.println(result.get(fn).toString());
			 }
		 }
	}
	
	/**
	 * 读取参数文件
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	private static List<Map<FieldName,Object>> readInParams(String filePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String[] nameArr = br.readLine().split(",");  //读取表头的名字
		
		List<Map<FieldName,Object>> list = new ArrayList();
		String paramLine = null;  //一行参数
		//循环读取  每次读取一行数据
		while((paramLine = br.readLine()) != null){
			Map<FieldName,Object> map = new HashMap<FieldName, Object>();
			String[] paramLineArr = paramLine.split(",");
//			一次循环处理一行数据
			for(int i=0; i<paramLineArr.length; i++){
				map.put(new FieldName(nameArr[i]), paramLineArr[i]); //将表头和值组成map 加入list中
			}
			list.add(map);
		}
		return list;
	}
	
}
