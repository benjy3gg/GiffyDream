package com.benjy3gg.giphydream;

import com.codingbuffalo.giphydream.service.AerialVideo;
import com.codingbuffalo.giphydream.service.VideoService;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

public class FetchTest {
	@Test
	public void fetchVideos() throws Exception {
		List<AerialVideo> videos = VideoService.fetchVideos();
		Assert.assertNotNull(videos);
	}
}