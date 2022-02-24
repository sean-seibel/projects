package view;

import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.util.DefaultMap;

public class SpriteMap<K> extends ImageView {

  private final Map<K, Image> map;

  public SpriteMap(Image def) {
    super();
    this.map = new DefaultMap<>(def);
  }

  public void put(K key, Image value) {
    map.put(key, value);
  }

  public void setByKey(K key) {
    this.setImage(map.get(key));
  }
}
