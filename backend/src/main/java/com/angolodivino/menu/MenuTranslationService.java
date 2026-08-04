package com.angolodivino.menu;

import java.util.List;

/** Provider-neutral translation boundary used by menu CRUD operations. */
public interface MenuTranslationService {
    List<String> translate(List<String> texts, String targetLanguage);
}
