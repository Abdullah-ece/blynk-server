import React from 'react';
import {MetadataSelect as Select} from 'components/Form';
import {Item, ItemsGroup} from "components/UI";
import {Button, Radio, Icon} from 'antd';
import {Field, change} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import _ from 'lodash';
import PropTypes from 'prop-types';
import Validation from 'services/Validation';
import {fromJS, Map, List} from 'immutable';
import {
  WIDGETS_SOURCE_TYPES_LIST,
  WIDGETS_CHART_TYPES_LIST,
  WIDGETS_CHART_TYPES,
} from 'services/Widgets';
import {
  SimpleContentEditable
} from 'components';

@connect((state) => ({
  dataStreams: fromJS(state.Product.edit.dataStreams.fields || []),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class Source extends React.Component {

  static propTypes = {
    form: PropTypes.string,

    index: PropTypes.number,

    source: PropTypes.instanceOf(Map),
    dataStreams: PropTypes.instanceOf(List),

    changeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.labelComponent = this.labelComponent.bind(this);
  }

  componentWillUpdate(nextProps) {
    if (nextProps.source.get('dataStreamId') !== this.props.source.get('dataStreamId') && this.props.dataStreams.length) {
      const stream = this.props.dataStreams.find(
        stream => parseInt(stream.get('id')) === parseInt(nextProps.source.get('dataStreamId'))
      );

      this.props.changeForm(this.props.form, `sources.${this.props.index}.dataStream`, stream.get('values'));
    }
  }

  getIconForChartByType(type) {
    if (type === WIDGETS_CHART_TYPES.LINE)
      return 'line-chart';

    if (type === WIDGETS_CHART_TYPES.DOTS)
      return 'dot-chart';

    // dot-chart bar-chart

  }

  labelComponent({input}) {
    return (
      <SimpleContentEditable
        className="modal-window-widget-settings-config-column-sources-source-header-name"
        value={input.value}
        onChange={input.onChange}/>
    );
  }

  chartTypeSelectComponent({input, getIconForChartByType}) {
    return (
      <Radio.Group value={input.value} onChange={input.onChange}>
        {WIDGETS_CHART_TYPES_LIST.map((chart) => (
          <Radio.Button value={chart.key} key={chart.key}>
            <Icon type={getIconForChartByType(chart.key)}/>
          </Radio.Button>
        ))}
      </Radio.Group>
    );
  }

  render() {

    const getNotFoundDataStreamContent = () => {

      if (this.props.dataStreams.length) return 'No Data Streams match your request';

      return 'Add Data Stream before setup chart';
    };

    const getLabelForChartTypeItem = () => {

      const name = _.find(WIDGETS_CHART_TYPES_LIST, ((item) => item.key === this.props.source.get('graphType')));

      if (!name) return `Please, select chart type`;

      return `Chart Type: ${name.value}`;
    };

    const sources = this.props.dataStreams.map((dataStream) => ({
      key: String(dataStream.get('id')),
      value: dataStream.getIn(['values', 'label']),
    })).toJS();

    return (
      <div className="modal-window-widget-settings-config-column-sources-source">
        <div className="modal-window-widget-settings-config-column-sources-source-header">
          <Field name={`sources.${this.props.index}.label`} component={this.labelComponent}/>

          <div className="modal-window-widget-settings-config-column-sources-source-header-tools">
            <Button size="small" icon="delete"/>
            <Button size="small" icon="copy"/>
            <Button size="small" icon="bars"/>
          </div>
        </div>
        <div className="modal-window-widget-settings-config-column-sources-source-type-select">
          <ItemsGroup>
            <Item label="Source" offset="medium">
              <Select name={`sources.${this.props.index}.sourceType`}
                      values={WIDGETS_SOURCE_TYPES_LIST}
                      placeholder="Choose Type"
                      validate={[Validation.Rules.required]}
                      style={{width: '100px'}}/>
            </Item>
            <Item label=" " offset="medium">
              <Select notFoundContent={getNotFoundDataStreamContent()}
                      name={`sources.${this.props.index}.dataStreamId`} values={sources}
                      placeholder="Choose Source"
                      validate={[Validation.Rules.required]}
                      style={{width: '100%'}}/>
            </Item>
          </ItemsGroup>
        </div>
        <div className="modal-window-widget-settings-config-column-sources-source-chart-type">
          <div className="modal-window-widget-settings-config-column-sources-source-chart-type-select">
            <Item label={getLabelForChartTypeItem()} offset="medium">
              <Field component={this.chartTypeSelectComponent} name={`sources.${this.props.index}.graphType`}
                     getIconForChartByType={this.getIconForChartByType}/>
            </Item>
          </div>
        </div>
      </div>
    );
  }

}

export default Source;
