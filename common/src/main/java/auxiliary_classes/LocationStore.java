package auxiliary_classes;

public class LocationStore {
    String langName; String langTag; String zoneId; String resourseFileName;
    public LocationStore(String langName, String langTag, String zoneId, String resourseFileName){
         this.langName = langName;
         this.langTag = langTag;
         this.zoneId = zoneId;
         this.resourseFileName = resourseFileName;
    }

    public String getLangName() {
        return langName;
    }

    public String getLangTag() {
        return langTag;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getResourseFileName() {
        return resourseFileName;
    }
}
