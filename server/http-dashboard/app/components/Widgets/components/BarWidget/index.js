import React from 'react';
import {
  Plotly
} from 'components';
import PropTypes from 'prop-types';
import './styles.less';
import {connect} from 'react-redux';
import {Map} from 'immutable';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class BarWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    typeOfData: PropTypes.number,

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  state = {
    data: []
  };

  layout = {

  };

  dataChartConfig = {

  };

  config = {
    displayModeBar: false
  };

  renderChartByParams(data = [], config = {}, layout = {}) {
    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={config} layout={layout}/>
      </div>
    );
  }

  renderDevicesByOrganization() {

    const data = [];

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    this.renderChartByParams(data, config, layout);

  }

  renderDevicesByProduct() {
    const data = [];

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    this.renderChartByParams(data, config, layout);
  }

  renderProductByOrganization() {
    const data = [];

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    this.renderChartByParams(data, config, layout);
  }

  render() {

    return (
      <div>Is there</div>
    );

    // if (parseInt(this.props.typeOfData) === 1)
    //   this.renderDevicesByOrganization();

    // if (parseInt(this.props.typeOfData) === 2)
    //   this.renderDevicesByProduct();

    // otherwise
    // this.renderProductByOrganization();
  }

}

export default BarWidget;
