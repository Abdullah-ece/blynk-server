import React from 'react';

import {Button, Icon, Popconfirm} from 'antd';

import {SortableHandle} from 'react-sortable-hoc';

import PropTypes from 'prop-types';

const DragHandler = SortableHandle(() => <Icon type="bars" className="cards-list--item--tools--move"/>);

class CardItem extends React.Component {

  static propTypes = {
    draggable: PropTypes.bool,
    copyable: PropTypes.bool,
    removeable: PropTypes.bool,

    onCopy: PropTypes.func,
    onRemove: PropTypes.func,

    children: PropTypes.any,

    cardId: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.handleCopy = this.handleCopy.bind(this);
    this.handleRemove = this.handleRemove.bind(this);
  }

  handleCopy() {
    if(typeof this.props.onCopy === 'function') {
      this.props.onCopy(this.props.cardId);
    }
  }

  handleRemove() {
    if(typeof this.props.onRemove === 'function') {
      this.props.onRemove(this.props.cardId);
    }
  }

  render() {

    const {
      draggable,
      copyable,
      removeable
    } = this.props;

    return (
      <div className="cards-list--item">
        { this.props.children }

        { ((draggable || copyable || removeable) && (
          <div className="cards-list--item--tools">
            { draggable && (<DragHandler/>) || (null)}
            { copyable && (<Button onClick={this.handleCopy} icon="copy" size="small" />) || (null)}
            { removeable && (
              <Popconfirm title="Are you sure you want to delete this role?"
                          okText="Yes"
                          cancelText="No"
                          onConfirm={this.handleRemove}
                          overlayClassName="danger">
                <Button icon="delete" size="small" />
              </Popconfirm>
            ) || (null)}
          </div>
        )) || (
          null
        )}

      </div>
    );
  }

}

export default CardItem;
