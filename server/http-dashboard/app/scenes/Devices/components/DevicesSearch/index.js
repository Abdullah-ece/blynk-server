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
    devicesSearchFormValues: propTypes.any,
    devicesSortValue: propTypes.string,
    devicesSortChange: propTypes.func,

    sortingOptions: propTypes.array,

    smartSearch: propTypes.bool,

    changeForm: React.PropTypes.func
  };

  state = {
    smartSearchSuggestions: undefined
  }

  findDeviceByField(fields=[], value, devices){
    const preparedValue = value.trim().toLowerCase();
    const results = {};

    for(let j in devices){
      const device = devices[j];
      for(let i in fields){
        const fieldValue = (device.get(fields[i]) || '').toLowerCase();
        if (fieldValue.indexOf(preparedValue) !== -1){
          if (!results[`device.${fields[i]}`]){
            results[`device.${fields[i]}`] = [];
          }
          results[`device.${fields[i]}`].push({ field: fields[i], parentType: 'device', device });
        }
      }
    }

    return results;
  }

  findDeviceByMetadata(value, devices){
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

  smartSearchTopics = {
    'device.name': 'Name',
    'device.boardType': 'Board Type',
    'device.orgName': 'Organization',
    'device.status': 'Status',
    'device.productName': 'Product'
  }

  smartSearchTagsCollection = {}

  pushTagsToReduxForm(tags){
    this.props.changeForm(DEVICES_SEARCH_FORM_NAME, 'tags', tags);
  }

  // refactor me gently
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

    const { devices } = this.props;
    const devicesArray = devices.toArray();
    const deviceResults = this.findDeviceByField([
      'name',
      'boardType',
      'orgName',
      'status',
      'productName'
    ], query, devicesArray);
    const metadataResults = this.findDeviceByMetadata(query, devicesArray);

    const options = _.map({
      ...deviceResults,
      ...metadataResults
    }, (results, i) => {
      const id = uuid();
      const devices = results.map(r => r.device.get('id'));
      const topic = this.smartSearchTopics[i] || i;
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

    return _.compact([
      // unfortunally there is a bug in rc-select that is used in ant 2.x.x and hotkeys won't work with
      // <OptGroup /> so we need to emulate this behaviour manually
      ...smartSearchSuggestions.filter(e => e.type === 'device').map(e => e.element),
      this.createLabelOption('Metadata', () => metadataSuggestions.length > 0),
      ...smartSearchSuggestions.filter(e => e.type === 'metadata').map(e => e.element)
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
