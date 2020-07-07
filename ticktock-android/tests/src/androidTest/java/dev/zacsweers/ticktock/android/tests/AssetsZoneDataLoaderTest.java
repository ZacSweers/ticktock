package dev.zacsweers.ticktock.android.tests;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.joining;

@RunWith(AndroidJUnit4.class)
public final class AssetsZoneDataLoaderTest {
  @Test
  public void normalAssetLoad() throws Exception {
    AssetsZoneDataLoader loader =
        AssetsZoneDataLoader.create(ApplicationProvider.getApplicationContext());

    try (InputStream is = loader.openData("testAssets/testasset.txt")) {
      String text =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
              .lines()
              .collect(joining("\n"));
      assertThat(text).isEqualTo("why can't I put this in androidTest/assets");
    }
  }
}
