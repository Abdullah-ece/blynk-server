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

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

  };

  constructor(props) {
    super(props);

  }

  render() {

    if(!this.props.widgets)
      return null;

    const widgets = {
      lg: this.props.widgets.map(
        (item) => item.set('i', (item.get('id')).toString())
      ).toJS()
    };

    return (
      <div className="product-manage-dashboard-grid">
        <Widgets editable={true} data={widgets} params={this.props.params}/>
      </div>
    );
  }

}

export default Grid;
