<%@  Language="C#" Class="Handler1" %>
    public class Handler1 : System.Web.IHttpHandler,System.Web.SessionState.IRequiresSessionState
    {

        public void ProcessRequest(System.Web.HttpContext Context)
        {
			{code}
        }

        public bool IsReusable
        {
            get
            {
                return false;
            }
        }
    }