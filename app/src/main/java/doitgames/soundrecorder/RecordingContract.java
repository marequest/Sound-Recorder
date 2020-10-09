package doitgames.soundrecorder;

public class RecordingContract {

    static final String TABLE_NAME = "Recordings";

    public static class Columns{
        public static final String _ID = "_ID";
        public static final String RECORDING_NAME = "Name";
        public static final String RECORDING_LENGTH = "Length";
        public static final String RECORDING_FILE_PATH = "FilePath";
        public static final String RECORDING_TIME_ADDED = "TimeAdded";
    }

}
