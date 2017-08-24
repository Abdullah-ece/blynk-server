import React from 'react';
import {Widgets} from 'components';
import './styles.less';

class Grid extends React.Component {

  render() {
    return (
      <div className="product-manage-dashboard-grid">
        <Widgets editable={true}/>
      </div>
    );
  }

}

export default Grid;
