package lhg.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by lhg on 2017/5/10.
 */

public class SimplePreference implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sp;
    Map<String, Entity> allEntities = new HashMap<>();
    List<OnEntityChangeListener> onEntityChangeListeners;
    Handler handler = new Handler(Looper.getMainLooper());
    protected Context context;

    public SimplePreference(Context context, String name) {
        this.context = context;
        this.sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.sp.registerOnSharedPreferenceChangeListener(this);
        this.onEntityChangeListeners = new ArrayList<>();
    }

    public void addOnEntityChangeListener(OnEntityChangeListener l) {
        synchronized (onEntityChangeListeners) {
            onEntityChangeListeners.add(l);
        }
    }

    public void removeOnEntityChangeListener(OnEntityChangeListener l) {
        synchronized (onEntityChangeListeners) {
            onEntityChangeListeners.remove(l);
        }
    }

    public void setAll(Task ...tasks) {
        SharedPreferences.Editor editor = sp.edit();
        for (Task t : tasks) {
            t.entity.set(editor, t.val);
        }
        editor.commit();
    }

    public List<Entity> allEntities() {
        return new ArrayList<>(allEntities.values());
    }


    public void initFromProperties(File file) {
        boolean fileExists = file.exists();
        if (!fileExists) {
            return;
        }

        InputStream is = null;
        InputStreamReader isr = null;
        try {
            is = new FileInputStream(file);
            isr = new InputStreamReader(is, "UTF-8");
            Properties properties = new Properties();
            properties.load(isr);
            for (Entity entity : allEntities.values()) {
                String value = properties.getProperty(entity.key());
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                Log.d("SimplePreference", " from file set " + entity.key() + " = " + value);
                entity.setRaw(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }
        }
    }



//    public void clearAll(Entity ...entities) {
//        SharedPreferences.Editor editor = sp.edit();
//        for (Entity t : entities) {
//            t.set(editor, null);
//        }
//        editor.commit();
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Entity entity = allEntities.get(key);
        if (entity == null) {
            return;
        }
        synchronized (onEntityChangeListeners) {
            for (OnEntityChangeListener l : onEntityChangeListeners) {
                l.onEntityChange(entity);
            }
        }
    }

    public class Task<ValueType> {
        Entity<ValueType> entity;
        ValueType val;

        public Task(Entity<ValueType> entity, ValueType val) {
            this.entity = entity;
            this.val = val;
        }
    }

    public class Entity_Boolean extends Entity<Boolean> {
        public Entity_Boolean(String key) {
            super(key);
        }

        @Override
        public Boolean get(Boolean def) {
            return sp.getBoolean(key, def == null ? false : def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, Boolean val) {
            editor.putBoolean(key, val);
        }
    }
    public class Entity_Integer extends Entity<Integer> {
        public Entity_Integer(String key) {
            super(key);
        }

        @Override
        public Integer get(Integer def) {
            return sp.getInt(key, def == null ? 0 : def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, Integer val) {
            editor.putInt(key, val);
        }
    }
    public class Entity_Long extends Entity<Long> {
        public Entity_Long(String key) {
            super(key);
        }

        @Override
        public Long get(Long def) {
            return sp.getLong(key, def == null ? 0L : def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, Long val) {
            editor.putLong(key, val);
        }
    }
    public class Entity_Float extends Entity<Float> {
        public Entity_Float(String key) {
            super(key);
        }

        @Override
        public Float get(Float def) {
            return sp.getFloat(key, def == null ? 0f : def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, Float val) {
            editor.putFloat(key, val);
        }
    }
    public class Entity_String extends Entity<String> {
        public Entity_String(String key) {
            super(key);
        }

        @Override
        public String get(String def) {
            return sp.getString(key, def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, String val) {
            editor.putString(key, val);
        }
    }

//    public class Entity_Json<T> extends Entity<T> {
//        TypeToken<T> typeToken;
//        public Entity_Json(String key, TypeToken<T> typeToken) {
//            super(key);
//            this.typeToken = typeToken;
//        }
//
//        @Override
//        public T get(T def) {
//            String json = sp.getString(key, "");
//            if (!TextUtils.isEmpty(json)) {
//                return gson.fromJson(json, typeToken.getType());
//            }
//            return null;
//        }
//
//        @Override
//        protected void setNotNull(SharedPreferences.Editor editor, T val) {
//            editor.setString(key, gson.toJson(val));
//        }
//    }

    public class Entity_StringSet extends Entity<Set> {
        public Entity_StringSet(String key) {
            super(key);
        }

        @Override
        public Set<String> get(Set def) {
            return sp.getStringSet(key, def);
        }

        @Override
        protected void setNotNull(SharedPreferences.Editor editor, Set val) {
            editor.putStringSet(key, val);
        }
    }

    public abstract class Entity<ValueType> {
        String key;

        private Entity(String key) {
            this.key = key;
            allEntities.put(key, this);
        }
        public final ValueType get() {
            return get(null);
        }
        public final boolean exist() {
            return sp.contains(key);
        }

        public abstract ValueType get(ValueType def);
        protected abstract void setNotNull(SharedPreferences.Editor editor, ValueType val);

        private final void set(SharedPreferences.Editor editor, ValueType val) {
            if (val == null) {
                editor.remove(key);
            } else {
                setNotNull(editor, val);
            }
        }

        public final boolean setRaw(String value) {
            Entity entity = this;
            if (entity instanceof SimplePreference.Entity_String) {
                return entity.set(value);
            } else if (entity instanceof Entity_Integer) {
                return  entity.set(Integer.valueOf(value));
            } else if (entity instanceof Entity_Long) {
                return  entity.set(Long.valueOf(value));
            } else if (entity instanceof Entity_Boolean) {
                return  entity.set("true".equalsIgnoreCase(value));
            } else if (entity instanceof Entity_Float) {
                return entity.set(Float.valueOf(value));
            } else {
                throw new RuntimeException("不支持" + this.getClass().getSimpleName());
            }
        }

        public final String getRaw() {
            Entity entity = this;
            if (!entity.exist()) {
                return null;
            }
            if (entity instanceof SimplePreference.Entity_String
                    || entity instanceof Entity_Integer
                    || entity instanceof Entity_Long
                    || entity instanceof Entity_Boolean
                    || entity instanceof Entity_Float) {
                return String.valueOf(entity.get());
            } else {
                throw new RuntimeException("不支持" + this.getClass().getSimpleName());
            }
        }

        public final boolean set(ValueType val) {
            SharedPreferences.Editor editor = sp.edit();
            set(editor, val);
            return editor.commit();
        }

        public final boolean setIfNull(ValueType val) {
            if (exist()) {
                return false;
            }
            return set(val);
        }

        public boolean remove() {
            return set(null);
        }

        public Task<ValueType> with(ValueType val) {
            return new Task<>(this, val);
        }

        public String key() {
            return key;
        }
    }

    public interface OnEntityChangeListener {
        void onEntityChange(Entity entity);
    }
}
