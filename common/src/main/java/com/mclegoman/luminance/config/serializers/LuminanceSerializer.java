/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config.serializers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.MarshallingUtils;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.util.SerializerUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class LuminanceSerializer implements Serializer {
	private final ConfigParser<CommentedConfig> parser = new TomlParser();
	private final ConfigWriter writer = new TomlWriter();
	private final String fileExtension;
	public LuminanceSerializer(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void serialize(Config config, OutputStream output) {
		this.writer.write(write(config, createCommentedConfig(), config.nodes()), output);
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void deserialize(Config config, InputStream input) {
		CommentedConfig read = this.parser.parse(input);
		for (TrackedValue<?> value : config.values()) {
			String key = SerializerUtils.getSerializedKey(config, value).toString();
			if (read.contains(key)) ((TrackedValue) value).setValue(MarshallingUtils.coerce(read.get(key), value.getDefaultValue(), (CommentedConfig commentedConfig, MarshallingUtils.MapEntryConsumer entryConsumer) -> commentedConfig.entrySet().forEach(entry -> entryConsumer.put(entry.getKey(), entry.getValue()))), false);
		}
	}
	private static List<Object> convertList(List<?> list) {
		List<Object> result = new ArrayList<>(list.size());
		for (Object value : list) result.add(convertAny(value));
		return result;
	}
	private static UnmodifiableCommentedConfig convertMap(ValueMap<?> map) {
		CommentedConfig result = createCommentedConfig();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			List<String> key = new ArrayList<>();
			key.add(entry.getKey());
			result.add(key, convertAny(entry.getValue()));
		}
		return result;
	}
	private static Object convertAny(Object value) {
		if (value instanceof ValueMap) return convertMap((ValueMap<?>)value);
		else if (value instanceof ValueList) return convertList((ValueList<?>)value);
		else return value instanceof ConfigSerializableObject ? convertAny(((ConfigSerializableObject<?>)value).getRepresentation()) : value;
	}
	private static CommentedConfig write(Config config, CommentedConfig commentedConfig, Iterable<ValueTreeNode> nodes) {
		for (ValueTreeNode node : nodes) {
			List<String> comments = new ArrayList<>();
			if (node.hasMetadata(Comment.TYPE)) for (String string : node.metadata(Comment.TYPE)) comments.add(string);
			ValueKey key = SerializerUtils.getSerializedKey(config, node);
			if (!(node instanceof TrackedValue<?> value)) write(config, commentedConfig, (ValueTreeNode.Section) node);
			else {
				Object defaultValue = value.getDefaultValue();
				Optional<String> var10000 = SerializerUtils.createEnumOptionsComment(defaultValue);
				Objects.requireNonNull(comments);
				var10000.ifPresent(comments::add);
				for (Constraint<?> item : value.constraints()) comments.add(item.getRepresentation());
				if (!(defaultValue instanceof CompoundConfigValue)) comments.add("default: " + defaultValue);
				commentedConfig.add(toNightConfigSerializable(key), convertAny(value.getRealValue()));
			}
			if (!comments.isEmpty()) commentedConfig.setComment(toNightConfigSerializable(key), " " + String.join("\n ", comments));
		}
		return commentedConfig;
	}
	private static CommentedConfig createCommentedConfig() {
		return InMemoryCommentedFormat.defaultInstance().createConfig(LinkedHashMap::new);
	}
	private static List<String> toNightConfigSerializable(ValueKey key) {
		List<String> listKey = new ArrayList<>();
		Objects.requireNonNull(listKey);
		key.forEach(listKey::add);
		return listKey;
	}
}
