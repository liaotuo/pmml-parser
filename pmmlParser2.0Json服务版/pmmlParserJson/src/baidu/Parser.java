package baidu;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.TargetField;
import org.jpmml.model.PMMLUtil;

import com.alibaba.fastjson.JSON;

/**
 * pmml Parser and predict
 * 
 * @time 2017-9-13 15:17:01
 * @author liaotuo
 *
 */
public class Parser {
    public Evaluator evaluator;
    private Map<FieldName, Object> paramsMap;
    private Map<String, Object> jsonMap;

    /**
     * load pmml init model
     * 
     * @author liaotuo
     * @param pmmlFileName
     */
    public Parser(String pmmlFileName) {
        PMML pmml = null;
        if (pmmlFileName != null) {
            try {
                File pmmlFile = new File(pmmlFileName);
                InputStream is = new FileInputStream(pmmlFile);
                pmml = PMMLUtil.unmarshal(is);
                is.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        ModelEvaluator<?> modelEvaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
        evaluator = (Evaluator) modelEvaluator;
        evaluator.verify();
        paramsMap = new HashMap<>(2000);
    }

    /**
     * prepare data
     * 
     * @param json
     */
    private void prepare(String json) {
        // clear paramsMap
        paramsMap.clear();
        // json to map
        jsonMap = JSON.parseObject(json);
        // prepare data
        List<InputField> inputFields = evaluator.getActiveFields();
        FieldName inputFieldName = null;
        Object rawValue = null;
        FieldValue inputFieldValue = null;
        for (InputField inputField : inputFields) {
            inputFieldName = inputField.getName();
            rawValue = jsonMap.get(inputFieldName.getValue());
            // because some varname is not used
            if (rawValue != null) {
                // Unsupported type transferred to string
                if (!(rawValue instanceof String || rawValue instanceof Integer || rawValue instanceof Float || rawValue instanceof Double)) {
                    rawValue = String.valueOf(rawValue);
                }
                inputFieldValue = inputField.prepare("".equals(rawValue.toString()) ? null : rawValue);
                paramsMap.put(inputFieldName, inputFieldValue);
            }
        }
    }

    /**
     * predict method
     * 
     * @param json
     * @return result
     */
    public String predict(String json) {
        // 1. do prepare
        prepare(json);
        // 2. do excute
        String result = excute();
        return result;
    }

    /**
     * do evaluate and get result
     * 
     * @author liaotuo
     * @return resultStr
     */
    private String excute() {
        Map<FieldName, ?> result = evaluator.evaluate(paramsMap);
        List<TargetField> targetFields = evaluator.getTargetFields();
        StringBuilder sb = new StringBuilder();
        for (TargetField targetField : targetFields) {
            FieldName targetFieldName = targetField.getName();

            sb.append(result.get(targetFieldName) + ",");
        }
        String resultStr = sb.toString();
        return resultStr;
    }
}
