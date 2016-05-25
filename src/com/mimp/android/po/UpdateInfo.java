package com.mimp.android.po;

public class UpdateInfo
{
        private String version;
        private String description;
        private String UpdateLog;
        private String url;
        
        public String getVersion()
        {
                return version;
        }
        public void setVersion(String version)
        {
                this.version = version;
        }
        public String getDescription()
        {
                return description;
        }
        public void setDescription(String description)
        {
                this.description = description;
        }
        public String getUpdateLog()
        {
                return UpdateLog;
        }
        public void setUpdateLog(String updatelog)
        {
                this.UpdateLog = updatelog;
        }
        public String getUrl()
        {
                return url;
        }
        public void setUrl(String url)
        {
                this.url = url;
        }
        
}
