package com.nv.cpmfcu.utility;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

@Component
public class XmlToJsonConverter {

	    public JSONObject convertXmlToJson(String xml) throws Exception {
	    	         JSONObject jsonObject = XML.toJSONObject(xml);
                   return jsonObject;
	    }
}

