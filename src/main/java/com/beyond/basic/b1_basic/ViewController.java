package com.beyond.basic.b1_basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// CORS 에러 방지를 위해 view 컨트롤러 생성
@Controller
@RequestMapping("/post-view")
public class ViewController {

    @GetMapping("/1_url-encoded")
    static String url(){
        return "1_url-encoded";
    }

    @GetMapping("/2-1_multipart-form-data")
    static String formData1(){
        return "2-1_multipart-form-data";
    }

    @GetMapping("/3-1_json")
    static String json1(){
        return "3-1_json";
    }

    @GetMapping("/3-2_json-nested")
    static String json2(){
        return "3-2_json-nested";
    }

    @GetMapping("/3-3_json-with-file")
    static String json3(){
        return "3-3_json-with-file";
    }
}
