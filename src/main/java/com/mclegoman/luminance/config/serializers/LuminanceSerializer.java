/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.config.serializers;

import folk.sisby.kaleido.lib.nightconfig.core.CommentedConfig;
import folk.sisby.kaleido.lib.nightconfig.core.InMemoryCommentedFormat;
import folk.sisby.kaleido.lib.nightconfig.core.UnmodifiableCommentedConfig;
import folk.sisby.kaleido.lib.nightconfig.core.io.ConfigParser;
import folk.sisby.kaleido.lib.nightconfig.core.io.ConfigWriter;
import folk.sisby.kaleido.lib.nightconfig.toml.TomlParser;
import folk.sisby.kaleido.lib.nightconfig.toml.TomlWriter;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import folk.sisby.kaleido.lib.quiltconfig.api.Constraint;
import folk.sisby.kaleido.lib.quiltconfig.api.MarshallingUtils;
import folk.sisby.kaleido.lib.quiltconfig.api.Serializer;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.*;
import folk.sisby.kaleido.lib.quiltconfig.impl.util.SerializerUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class LuminanceSerializer implements Serializer {
	public static final LuminanceSerializer propertiesInstance = new LuminanceSerializer("properties");
	private final ConfigParser<CommentedConfig> parser = new TomlParser();
	private final ConfigWriter writer = new TomlWriter();
	private final String fileExtension;
	public LuminanceSerializer(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void serialize(folk.sisby.kaleido.lib.quiltconfig.api.Config config, OutputStream outputStream) {
		this.writer.write(write(config, createCommentedConfig(), config.nodes()), outputStream);
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void deserialize(folk.sisby.kaleido.lib.quiltconfig.api.Config config, InputStream inputStream) {
		CommentedConfig read = this.parser.parse(inputStream);
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
