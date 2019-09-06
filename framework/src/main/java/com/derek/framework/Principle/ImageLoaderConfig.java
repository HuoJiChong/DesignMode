package com.derek.framework.Principle;

public class ImageLoaderConfig {

    public static class DisplayConfig{
        public int loadingResId;
        public int failedResId;

        public DisplayConfig(int loadingResId, int failedResId) {
            this.loadingResId = loadingResId;
            this.failedResId = failedResId;
        }

        public DisplayConfig() {
        }

    }

    DisplayConfig displayConfig = new DisplayConfig();

    ImageCache cache = new MemCache();

    int threadCount = Runtime.getRuntime().availableProcessors() - 1;

    public ImageLoaderConfig setDisplayConfig(DisplayConfig displayConfig) {
        this.displayConfig = displayConfig;
        return this;
    }

    public ImageLoaderConfig setCache(ImageCache cache) {
        this.cache = cache;
        return this;
    }

    public ImageLoaderConfig setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }


    public static class Builder{

        DisplayConfig displayConfig = new DisplayConfig();

        ImageCache cache = new MemCache();

        int threadCount = Runtime.getRuntime().availableProcessors() - 1;

        public Builder setDisplayConfig(DisplayConfig displayConfig) {
            this.displayConfig = displayConfig;
            return this;
        }

        public Builder setCache(ImageCache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder setLoadingResId(int resid){
            displayConfig.loadingResId = resid;
            return this;
        }

        public Builder setFailedResId(int resId){
            displayConfig.failedResId = resId;
            return this;
        }

        public void apply(ImageLoaderConfig config){
            config.cache = this.cache;
            config.displayConfig = this.displayConfig;
            config.threadCount = this.threadCount;
        }

        public ImageLoaderConfig create(){
            ImageLoaderConfig config = new ImageLoaderConfig();
            apply(config);
            return config;
        }
    }
}
