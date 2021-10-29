package com.baidu.fbu.mtp.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.model.ResultBody;

public class ResultUtilTest {
    
    @Test
    public void testSuccess() {
        ResultBody resultBody = (ResultBody) ResultUtil.success("result");
        System.out.println(resultBody.getResult());
    }
    
    @Test
    public void testError() {
        ResultBody resultBody = (ResultBody) ResultUtil.error(2, null);
        System.out.println(resultBody.getMsg());
    }
    
    @Test
    public void testErrorV() {
        ResultBody resultBody = (ResultBody) ResultUtil.error(ResultCode.AUTH_ERROR, "");
        System.out.println(resultBody.getMsg());
    }
    
    @Test
    public void testPrintErrorMsg() throws IOException {
        String result = "{\"status\":-103,\"msg\":\"认证失败[]\",\"result\":{}}";
        HttpServletResponse response = createMock(HttpServletResponse.class);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        expectLastCall().anyTimes();
        PrintWriter out = createMock(PrintWriter.class);
        out.print(result);
        out.close();
        expectLastCall().anyTimes();
        replay(out);
        expect(response.getWriter()).andReturn(out);
        replay(response);
        ResultUtil.printErrorMsg(response, ResultCode.AUTH_ERROR, "");
    }
    
    @Test
    public void testPrintErrorMsgException() throws IOException {
        HttpServletResponse response = createMock(HttpServletResponse.class);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        expectLastCall().anyTimes();
        expect(response.getWriter()).andReturn(null);
        replay(response);
        ResultUtil.printErrorMsg(response, ResultCode.AUTH_ERROR, "");
    }
}
