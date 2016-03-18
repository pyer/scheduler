package ab;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public final class Do extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

	@Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> m = new HashMap<String, String>();
        try (PrintWriter writer = response.getWriter()) {

        	String[] parts = request.getQueryString().split("&");
        	
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>Do</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>Request</h1>");
            writer.println("<p>URI   : " + request.getRequestURI() + "</p>");
            writer.println("<p>URL   : " + request.getRequestURL() + "</p>");
            writer.println("<p>Query : " + request.getQueryString() + "</p>");

            for(Integer i=0; i<parts.length; i++) {
            	String[] sub = parts[i].split("=");
            	m.put(sub[0], sub[1]);
            	writer.println("<p>*&nbsp;" + sub[0] + "-->" + sub[1] + "</p>");
            }
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
