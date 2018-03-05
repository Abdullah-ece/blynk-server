import React from 'react';
import propTypes from 'prop-types';
import {reduxForm, change, getFormValues} from 'redux-form';
import _ from 'lodash';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Input, Item, ItemsGroup} from 'components/UI';
import { DEVICES_SEARCH_FORM_NAME } from 'services/Devices';
import { v4 as uuid } from 'node-uuid';
import { Select } from 'antd';
const { Option } = Select;
import { fromJS, List } from 'immutable';
import './styles.less';

const SORTING_OPTIONS_SIZE = 100;
const INPUTS_MARGIN = 4;

@connect(state => ({
  smartSearch: state.Storage.deviceSmartSearch,
  devices: state.Devices.get('devices'),
  products: state.Product.products,
  devicesSearchFormValues: fromJS(getFormValues(DEVICES_SEARCH_FORM_NAME)(state) || {}),
}), dispatch => ({
  changeForm: bindActionCreators(change, dispatch)
}))
@reduxForm({
  form: DEVICES_SEARCH_FORM_NAME,
  initialValues: {
    name: '',
    tags: []
  }
})
class DevicesSearch extends React.Component {
  static propTypes = {
    devices: propTypes.instanceOf(List),
    products: propTypes.instanceOf(Array),
    devicesSearchFormValues: propTypes.any,
    devicesSortValue: propTypes.string,
    devicesSortChange: propTypes.func,

    sortingOptions: propTypes.array,

    smartSearch: propTypes.bool,

    changeForm: React.PropTypes.func
  };

  static smartSearchTopics = {
    'device.name': 'Name',
    'device.boardType': 'Board Type',
    'device.orgName': 'Organization',
    'device.status': 'Status',
    'device.productName': 'Product',
    'dashboard.label': 'Label',
    'dashboard.dataType': 'Data Type',
    'dashboard.alignment': 'Alignment',
    'datastream.pin': 'Pin',
    'datastream.pinType': 'Pin Type',
    'datastream.min': 'Min',
    'datastream.max': 'Max',
    'datastream.label': 'Label',
    'datastream.units': 'Units'
  };

  state = {
    smartSearchSuggestions: undefined
  }

  findDeviceByField(fields=[], value='', devices=[]){
    const preparedValue = value.trim().toLowerCase();
    const results = {};

    for(let j in devices){
      const device = devices[j];
      for(let i in fields){
        const field = fields[i];
        const fieldValue = (device.get(field) || '').toLowerCase();

        if (fieldValue.indexOf(preparedValue) !== -1){
          if (!results[`device.${field}`]){
            results[`device.${field}`] = [];
          }
          results[`device.${field}`].push({ field, parentType: 'device', device });
        }
      }
    }

    return results;
  }

  findDeviceByMetadata(value='', devices=[]){
    const preparedValue = value.trim().toLowerCase();
    const results = {};

    for(let j in devices){
      const device = devices[j];
      const metadata = device.get('metaFields');

      if (!metadata || metadata.size === 0){
        continue;
      }

      const metadataArray = metadata.toJS();

      for(let i in metadataArray){
        const metadataEntity = metadataArray[i];
        const fieldValue = String(metadataEntity.value).toLowerCase();

        if (fieldValue.indexOf(preparedValue) !== -1){
          if (!results[metadataEntity.name]){
            results[metadataEntity.name] = [];
          }

          results[metadataEntity.name].push({ field: metadataEntity.name, parentType: 'metadata', device });
        }
      }
    }

    return results;
  }

  findDeviceByDashboard(fields=[], value='', devices=[]){
    const preparedValue = value.trim().toLowerCase();
    const results = {};

    for(let j in devices){
      const device = devices[j];
      const widgets = device.toJS().webDashboard.widgets;
      for(let k in widgets){
        const widget = widgets[k];

        for(let i in fields){
          const field = fields[i];
          const fieldValue = (widget[field] || '').toLowerCase();

          if (fieldValue.indexOf(preparedValue) !== -1){
            if (!results[`dashboard.${field}`]){
              results[`dashboard.${field}`] = [];
            }
            results[`dashboard.${field}`].push({ field, parentType: 'dashboard', device });
          }
        }
      }
    }

    return results;
  }

  findDeviceByDataStream(fields=[], value='', devices=[], products=[]){
    const preparedValue = value.trim().toLowerCase();
    const results = {};

    for(let j in devices){
      const device = devices[j];
      
      const productId = device.get('productId');
      const product = products.filter(p => p.id === productId)[0];

      if (!product || !product.dataStreams || product.dataStreams.length === 0){
        continue;
      }

      const dataStreams = product.dataStreams || [];
      for(let k in dataStreams){
        const dataStream = dataStreams[k];
        for(let i in fields){
          const field = fields[i];
          const fieldValue = (String(dataStream[field]) || '').toLowerCase();

          if (fieldValue.indexOf(preparedValue) !== -1){
            if (!results[`datastream.${field}`]){
              results[`datastream.${field}`] = [];
            }
            results[`datastream.${field}`].push({ field, parentType: 'datastream', device });
          }
        }
      }
    }

    return results;
  }

  smartSearchTagsCollection = {}

  pushTagsToReduxForm(tags){
    this.props.changeForm(DEVICES_SEARCH_FORM_NAME, 'tags', tags);
  }

  handleTagsChange(value=[]){
    // last tag is the one that was added recently
    // we should check it
    const lastTagAsString = value[value.length - 1];

    if (!lastTagAsString){
      // looks like the last tag was removed
      return this.pushTagsToReduxForm([]);
    }

    try{
      // check for tag in the list
      const lastTag = JSON.parse(lastTagAsString);

      const { smartSearchSuggestions=[] } = this.state;
      const isLastTagInTheList = smartSearchSuggestions.filter(s => s.id === lastTag.id).length > 0;

      if (isLastTagInTheList){
        this.smartSearchTagsCollection[lastTag.id] = lastTag;
      }
    } catch(e){
      // nothing to add
    }

    const tagsToPush = _.chain(value).map(t => {
      try{
        const id = JSON.parse(t).id;
        return this.smartSearchTagsCollection[id];
      } catch(e){
        // we won't be able to find tag by its ID based on various conditions(related to antd data model)
        // in this case we also trying to find it by its TITLE
        return _.filter(this.smartSearchTagsCollection, tag => tag.title === t)[0] || null;
      }
    }).uniq().compact().value();
    this.pushTagsToReduxForm(tagsToPush);
  }

  handleTagsSearch(query=''){
    if (!query){
      return this.setState({ smartSearchSuggestions: undefined });
    }

    const { devices, products } = this.props;
    const devicesArray = devices.toArray();
    const productsArray = products;
    const deviceResults = this.findDeviceByField([
      'name',
      'boardType',
      'orgName',
      'status',
      'productName'
    ], query, devicesArray);
    const metadataResults = this.findDeviceByMetadata(query, devicesArray);
    const dashboardResults = this.findDeviceByDashboard([
      'label',
      'dataType',
      'alignment'
    ], query, devicesArray);
    const dataStreamResults = this.findDeviceByDataStream([
      'pin',
      'pinType',
      'min',
      'max',
      'label',
      'units'
    ], query, devicesArray, productsArray);

    const options = _.map({
      ...deviceResults,
      ...metadataResults,
      ...dashboardResults,
      ...dataStreamResults
    }, (results, i) => {
      const id = uuid();
      const devices = results.map(r => r.device.get('id'));
      const topic = DevicesSearch.smartSearchTopics[i] || i;
      const title = `${topic}: ${query} [${results.length}]`;
      // query should always be in model since antd won't show any option without query in its value
      // query is included in title now
      // this model are going to be saved into the redux form store once tag will be chosen
      const model = { id, title, devices, type: results[0].parentType };
      // only way to pass the model is to stringify it since antd supports strings only in value attribute
      const element = <Option key={i} value={JSON.stringify(model)}>{ title }</Option>;

      return { ...model, element };
    });

    this.setState({ smartSearchSuggestions: options });
  }

  renderSortingOptions(){
    const { sortingOptions, devicesSortValue, devicesSortChange } = this.props;

    return (<Select style={{width: SORTING_OPTIONS_SIZE, maxWidth: SORTING_OPTIONS_SIZE, minWidth: SORTING_OPTIONS_SIZE}}
                    optionLabelProp={'label'}
                    value={devicesSortValue}
                    onChange={devicesSortChange}
                    dropdownMatchSelectWidth={false}>
      {sortingOptions && sortingOptions.map(option => (
        <Option key={option.key}
                       label={option.label}>
          {option.text}
        </Option>
      ))}
    </Select>);
  }

  createLabelOption(label, condition){
    let conditionResult = true;

    if (typeof condition === 'function'){
      conditionResult = !!condition();
    }

    if (conditionResult){
      return <Option className="smart-search--label-option" disabled key={label}>{ label }</Option>;
    }

    return null;
  }

  renderSuggestionOptions(smartSearchSuggestions){
    if (!smartSearchSuggestions){
      return null;
    }

    const metadataSuggestions = smartSearchSuggestions.filter(e => e.type === 'metadata');
    const dashboardSuggestions = smartSearchSuggestions.filter(e => e.type === 'dashboard');
    const dataStreamSuggestions = smartSearchSuggestions.filter(e => e.type === 'datastream');

    return _.compact([
      // unfortunally there is a bug in rc-select that is used in ant 2.x.x and hotkeys won't work with
      // <OptGroup /> so we need to emulate this behaviour manually
      ...smartSearchSuggestions.filter(e => e.type === 'device').map(e => e.element),
      this.createLabelOption('Metadata', () => metadataSuggestions.length > 0),
      ...metadataSuggestions.map(e => e.element),
      this.createLabelOption('Dashboard', () => dashboardSuggestions.length > 0),
      ...dashboardSuggestions.map(e => e.element),
      this.createLabelOption('Data Streams', () => dataStreamSuggestions.length > 0),
      ...dataStreamSuggestions.map(e => e.element)
    ]);
  }

  render() {
    const { smartSearch, devicesSearchFormValues } = this.props;
    const { smartSearchSuggestions } = this.state;

    const tags = devicesSearchFormValues.get('tags');
    const tagTitles = tags ? tags.map(t => t.get('title')).toJS() : [];

    // slightly different layout when Smart Search are enabled
    if (smartSearch){
      const selectorWidth = `calc(100% - ${SORTING_OPTIONS_SIZE + INPUTS_MARGIN}px)`;

      return (<div className="devices-search">
                <Select style={{ width: selectorWidth, marginRight: INPUTS_MARGIN }}
                        className="smart-search--select"
                        mode="tags"
                        filterOption={false}
                        value={tagTitles}
                        placeholder="Smart Search"
                        dropdownClassName="smart-search--dropdown"
                        onChange={this.handleTagsChange.bind(this)}
                        onSearch={this.handleTagsSearch.bind(this)}>
                  {this.renderSuggestionOptions(smartSearchSuggestions)}
                </Select>
                {this.renderSortingOptions()}
            </div>);
    }

    return (
      <div className="devices-search">
        <ItemsGroup>
          <Item>
            <Input style={{width: '100%'}}
                   name="name"
                   mode="multiple"
                   placeholder="Search by device name"
                   autoComplete="off"
                   notFoundContent="Search is on development"/>
            </Item>
          <Item style={{width: SORTING_OPTIONS_SIZE}}>{this.renderSortingOptions()}</Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default DevicesSearch;
