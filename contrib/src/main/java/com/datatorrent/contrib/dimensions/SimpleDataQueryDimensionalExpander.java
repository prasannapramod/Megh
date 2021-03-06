/**
 * Copyright (c) 2016 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.contrib.dimensions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.apex.malhar.lib.dimensions.DimensionsDescriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.datatorrent.lib.appdata.gpo.GPOMutable;
import com.datatorrent.lib.appdata.schemas.DataQueryDimensionalExpander;
import com.datatorrent.lib.appdata.schemas.FieldsDescriptor;


/**
 *
 * @since 3.3.0
 */

public class SimpleDataQueryDimensionalExpander implements DataQueryDimensionalExpander
{
  private final Map<String, Collection<Object>> seenKeyValues;

  public SimpleDataQueryDimensionalExpander(Map<String, Collection<Object>> seenEnumValues)
  {
    this.seenKeyValues = Preconditions.checkNotNull(seenEnumValues);
  }

  @Override
  public List<GPOMutable> createGPOs(Map<String, Set<Object>> keyToValues,
      FieldsDescriptor fd)
  {
    //Unclean work around until helper method in FieldsDescriptor is added
    List<String> fields = Lists.newArrayList(fd.getFieldList());
    fields.remove(DimensionsDescriptor.DIMENSION_TIME);
    fields.remove(DimensionsDescriptor.DIMENSION_TIME_BUCKET);

    List<GPOMutable> results = Lists.newArrayList();

    if (fields.isEmpty()) {
      results.add(new GPOMutable(fd));
      return results;
    } else {
      for (String key : fields) {
        if (seenKeyValues.get(key).isEmpty() && keyToValues.get(key).isEmpty()) {
          return results;
        }
      }
    }

    createKeyGPOsHelper(0, keyToValues, fd, fields, null, results);
    return results;
  }

  private void createKeyGPOsHelper(int index,
      Map<String, Set<Object>> keyToValues,
      FieldsDescriptor fd,
      List<String> fields,
      GPOMutable gpo,
      List<GPOMutable> resultGPOs)
  {
    String key = fields.get(index);
    Collection<Object> vals = keyToValues.get(key);

    if (vals.isEmpty()) {
      vals = seenKeyValues.get(key);
    }

    for (Object val : vals) {
      GPOMutable gpoKey;

      if (index == 0) {
        gpoKey = new GPOMutable(fd);
      } else {
        gpoKey = new GPOMutable(gpo);
      }

      gpoKey.setFieldGeneric(key, val);

      if (index == fields.size() - 1) {
        resultGPOs.add(gpoKey);
      } else {
        createKeyGPOsHelper(index + 1, keyToValues, fd, fields, gpoKey, resultGPOs);
      }
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(SimpleDataQueryDimensionalExpander.class);
}
