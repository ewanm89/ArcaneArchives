package com.aranaira.arcanearchives.util;

import com.aranaira.arcanearchives.ArcaneArchives;
import com.aranaira.arcanearchives.util.ColorUtils.Color;
import it.unimi.dsi.fastutil.ints.Int2IntMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TintUtils {
	public static Path FILE_PATH;
	public static Int2IntOpenHashMap CACHE = null;

	public static void init () {
		FILE_PATH = new File(new File(ArcaneArchives.configDirectory, ArcaneArchives.MODID), "echo_colors.txt").toPath();
		CACHE = new Int2IntOpenHashMap();
		CACHE.defaultReturnValue(-1);
		if (!readData() || CACHE.isEmpty()) {
			generate();
			writeData();
		}
	}

	public static int getColor (ItemStack stack) {
		return CACHE.get(RecipeItemHelper.pack(stack));
	}

	public static boolean readData () {
		CACHE.clear();
		List<String> lines;
		try {
			lines = Files.readAllLines(FILE_PATH);
		} catch (IOException e) {
			return false;
		}

		for (String line : lines) {
			String[] split = line.split(",");
			if (split.length != 2) {
				continue;
			}

			int item = Integer.parseInt(split[0]);
			int color = Integer.parseInt(split[1]);
			CACHE.put(item, color);
		}

		return true;
	}

	public static void writeData () {
		List<String> output = new ArrayList<>();
		for (Entry x : CACHE.int2IntEntrySet()) {
			output.add(x.getIntKey() + "," + x.getIntValue());
		}
		try {
			Files.write(FILE_PATH, output);
		} catch (IOException e) {
			ArcaneArchives.logger.info("Unable to write colour values on the client. Whoops!");
		}
	}

	public static void generate () {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

		for (ItemStack toDuplicate : DuplicationUtils.getOresToDuplicate()) {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(toDuplicate);
			int packed = RecipeItemHelper.pack(result);
			IBakedModel model = renderItem.getItemModelWithOverrides(result, null, null);
			BakedQuad quad = null;
			for (BakedQuad q : model.getQuads(null, null, 0)) {
				if (q.getFace() == EnumFacing.SOUTH) {
					quad = q;
					break;
				}
			}
			if (quad == null) {
				continue;
			}
			TextureAtlasSprite sprite = quad.getSprite();
			String[] split = sprite.getIconName().split(":");
			String resource = "textures/" + split[1] + ".png";
			ResourceLocation rl = new ResourceLocation(split[0], resource);

			BufferedImage image;
			InputStream stream;
			try {
				stream = manager.getResource(rl).getInputStream();
			} catch (IOException ignored) {
				continue;
			}

			try {
				image = ImageIO.read(stream);
			} catch (IOException e) {
				continue;
			}

			WritableRaster raster = image.getRaster();

			int[] reds = new int[16];
			int[] greens = new int[16];
			int[] blues = new int[16];

			int[] temp;

			int i = 0;
			for (int x = 6; x <= 9; x++) {
				for (int y = 6; y <= 9; y++) {
					temp = raster.getPixel(x, y, (int[]) null);
					reds[i] = temp[0];
					greens[i] = temp[1];
					blues[i] = temp[2];
					i++;
				}
			}

			int red = Arrays.stream(reds).sum() / 16;
			int green = Arrays.stream(greens).sum() / 16;
			int blue = Arrays.stream(blues).sum() / 16;

			Color c = new Color(red, green, blue, 255);
			CACHE.put(packed, c.toInteger());
		}
	}
}