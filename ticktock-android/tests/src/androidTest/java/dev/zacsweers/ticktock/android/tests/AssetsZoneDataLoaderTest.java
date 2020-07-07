package dev.zacsweers.ticktock.android.tests;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.source;

@RunWith(AndroidJUnit4.class)
public final class AssetsZoneDataLoaderTest {
  @Test
  public void normalAssetLoad() throws Exception {
    AssetsZoneDataLoader loader =
        AssetsZoneDataLoader.create(ApplicationProvider.getApplicationContext());

    try (BufferedSource is = buffer(source(loader.openData("testAssets/testasset.txt")))) {
      String text = is.readUtf8();
      assertThat(text).isEqualTo("why can't I put this in androidTest/assets");
    }
  }
}
