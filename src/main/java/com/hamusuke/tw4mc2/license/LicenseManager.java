package com.hamusuke.tw4mc2.license;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LicenseManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<License> LICENSE_LIST = Lists.newArrayList();

    private LicenseManager() {
        throw new IllegalStateException();
    }

    public static void registerLicense(ResourceLocation location, int width, String translationKey) {
        try {
            List<String> list = getLicenseTextList(location);
            registerLicense(new License(location, list, width, translationKey));
        } catch (IOException e) {
            LOGGER.warn("Couldn't load license file", e);
        }
    }

    private static void registerLicense(License license) {
        String info = String.format("Registering License: %s:%s", license.getTextLocation().getNamespace(), license.getTextLocation().getPath());
        LOGGER.info(info);
        TwitterForMC2.addMessage(info);
        LICENSE_LIST.add(license);
    }

    public static ImmutableList<License> getLicenseList() {
        return ImmutableList.copyOf(LICENSE_LIST);
    }

    private static List<String> getLicenseTextList(ResourceLocation location) throws IOException {
        InputStream inputStream = LicenseManager.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath());
        if (inputStream == null) {
            throw new IOException("License file not found");
        }

        return IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
    }
}
