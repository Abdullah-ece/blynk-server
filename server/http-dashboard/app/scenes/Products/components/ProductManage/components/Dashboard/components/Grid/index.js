import React from 'react';
import {
  Widgets
} from 'components';
import PropTypes from 'prop-types';
import {
  List
} from 'immutable';
import './styles.less';

class Grid extends React.Component {

  static propTypes = {
    widgets: PropTypes.instanceOf(List),

    onChange: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(widgets) {
    this.props.onChange(widgets);
  }

  render() {

    const widgets = {
      lg: this.props.widgets.map(
        (item, i) => item.set('i', (i).toString()).set('minW', 2)
      ).toJS()
    };

    return (
      <div className="product-manage-dashboard-grid">
        <Widgets editable={true} data={widgets} onChange={this.handleChange}/>
      </div>
    );
  }

}

export default Grid;
