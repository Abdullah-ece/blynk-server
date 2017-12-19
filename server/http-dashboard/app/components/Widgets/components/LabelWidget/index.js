import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Map, fromJS} from 'immutable';
import {Icon} from 'antd';
import {WIDGETS_LABEL_TEXT_ALIGNMENT} from 'services/Widgets';
import './styles.less';

import LabelWidgetSettings from './settings';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class LabelWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.generateData = this.generateData.bind(this);
  }

  getTextAlignmentClassNameByAlignment(alignment) {
    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT)
      return 'widgets--widget-web-label--alignment-left';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER)
      return 'widgets--widget-web-label--alignment-center';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT)
      return 'widgets--widget-web-label--alignment-right';
  }

  renderLabelByParams(params = {alignment: WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT, value: null, suffix: null}) {

    const alignmentClassName = this.getTextAlignmentClassNameByAlignment(params.alignment);

    return (
      <div className={`widgets--widget-web-label ${alignmentClassName}`}>
        {params.value}{params.suffix}
      </div>
    );
  }

  renderFakeDataLabel() {
    return (
      <div className="widgets--widget-web-label">
        Label
      </div>
    );
  }

  generateData(source, sourceIndex) {
    if (!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.widgets.getIn([
      String(this.props.params.id),
      String(this.props.data.id),
      String(sourceIndex)
    ]);

    if (!pin)
      return null;

    return pin.get('data').last().get('y');
  }

  renderRealDataLabel() {

    if (!this.props.data.sources || !this.props.data.sources.length)
      return (<div>No data</div>);

    if (!this.props.widgets.hasIn([String(this.props.params.id), 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return (<Icon type="loading"/>);

    const sources = fromJS(this.props.data.sources);

    const dataSources = sources.map(this.generateData).filter((source) => source !== null);

    return this.renderLabelByParams({
      value: dataSources.get(0),
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment
    });
  }

  render() {
    if (this.props.fetchRealData)
      return this.renderRealDataLabel();

    return this.renderFakeDataLabel();
  }

}

LabelWidget.Settings = LabelWidgetSettings;

export default LabelWidget;
