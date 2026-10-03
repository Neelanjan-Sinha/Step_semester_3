public class Main {

    interface Playable {
        String play();
        String play(int fromSecond);
        String pause();
    }

    static abstract class MediaFile {
        private static int nextFileNumber = 1001;
        private final String fileId;

        protected MediaFile() {
            fileId = "MF-" + nextFileNumber++;
        }

        public abstract String getFormatInfo();

        public String getFileId() {
            return fileId;
        }
    }

    static class AudioFile extends MediaFile implements Playable {
        private final String title;

        public AudioFile(String title) {
            super();
            this.title = title;
        }

        @Override
        public String play() {
            return "Playing audio: " + title;
        }

        @Override
        public String play(int fromSecond) {
            int minutes = fromSecond / 60;
            int seconds = fromSecond % 60;

            return String.format(
                    "Playing audio: %s from %d:%02d",
                    title, minutes, seconds);
        }

        @Override
        public String pause() {
            return "Paused audio: " + title;
        }

        @Override
        public String getFormatInfo() {
            return "Audio file, ID: " + getFileId();
        }
    }

    static class Podcast implements Playable {
        private final String showName;
        private final int episodeNumber;

        public Podcast(String showName, int episodeNumber) {
            if (episodeNumber <= 0) {
                throw new IllegalArgumentException(
                        "Episode number must be positive");
            }

            this.showName = showName;
            this.episodeNumber = episodeNumber;
        }

        @Override
        public String play() {
            return "Streaming episode " + episodeNumber
                    + " of " + showName;
        }

        @Override
        public String play(int fromSecond) {
            int minutes = fromSecond / 60;
            int seconds = fromSecond % 60;

            return String.format(
                    "Streaming episode %d of %s from %d:%02d",
                    episodeNumber, showName, minutes, seconds);
        }

        @Override
        public String pause() {
            return "Paused episode " + episodeNumber
                    + " of " + showName;
        }
    }

    static void launchAll(Playable[] items) {
        for (Playable item : items) {
            System.out.println(item.play());
        }
    }

    public static void main(String[] args) {

        AudioFile a = new AudioFile("Morning Jazz");

        System.out.println(a.play());
        System.out.println(a.play(30));
        System.out.println(a.getFormatInfo());

        Podcast p = new Podcast("Tech Talk", 12);
        System.out.println(p.play());

        // Upcasting: AudioFile stored in Playable reference
        Playable ref = a;
        System.out.println(ref.play());

        launchAll(new Playable[]{ref, p});
    }
}
