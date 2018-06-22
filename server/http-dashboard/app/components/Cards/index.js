import React from 'react';

import {CardItem} from './components';
import PropTypes from 'prop-types';
import _ from 'lodash';

import {
  SortableContainer,
  SortableElement,
  // arrayMove
} from 'react-sortable-hoc';

import './styles.less';

class Card extends React.Component {

  static propTypes = {
    onSortEnd  : PropTypes.func,
    onSortStart: PropTypes.func,

    sortableListOptions: PropTypes.object,

    children: PropTypes.oneOfType([
      PropTypes.element,
      PropTypes.arrayOf(PropTypes.element)
    ])
  };

  constructor(props) {
    super(props);

    this.onSortStart= this.onSortStart.bind(this);
    this.onSortEnd = this.onSortEnd.bind(this);
  }

  SortableItem = SortableElement(({item}) => {
    return item;
  });

  SortableList = SortableContainer(({items}) => {
    return (
      <div>
        {items.map((item, index) => {
          return (
            <this.SortableItem key={`item-${item.key}`} index={index} item={item}/>
          );
        })}
      </div>
    );
  });

  onSortEnd({oldIndex, newIndex}) {

    const draggableList = (Array.isArray(this.props.children) ? this.props.children : [this.props.children]).filter((item) => {
      return item && item.props && item.props.draggable;
    });

    const oldKey = draggableList[oldIndex].key;
    const newKey = draggableList[newIndex].key;

    const oIndex = _.findIndex(this.props.children, (child) => child.key === oldKey);
    const nIndex = _.findIndex(this.props.children, (child) => child.key === newKey);

    if(typeof this.props.onSortEnd === 'function') {
      this.props.onSortEnd({
        oldIndex: oIndex,
        newIndex: nIndex,
      });
    }
  }

  onSortStart({node, index, collection}, event) {
    const draggableList = (Array.isArray(this.props.children) ? this.props.children : [this.props.children]).filter((item) => {
      return item && item.props && item.props.draggable;
    });

    const sortItemKey = draggableList[index].key;

    const sortIndex = _.findIndex(this.props.children, (child) => child.key === sortItemKey);

    if(typeof this.props.onSortStart === 'function') {
      this.props.onSortStart({
        node,
        collection,
        index: sortIndex,
      }, event);
    }
  }

  render() {

    const {
      sortableListOptions
    } = this.props;

    let items = [];
    let itemsDraggable = [];
    let itemsStatic = [];

    if (!Array.isArray(this.props.children)) {
      items = [this.props.children];
    } else {
      items = this.props.children;
    }

    items.forEach((item) => {
      if (item && item.props && item.props.draggable) {
        itemsDraggable.push(item);
      } else {
        itemsStatic.push(item);
      }
    });

    return (
      <div className="cards-list">

        {itemsStatic}

        <this.SortableList
          items={itemsDraggable}
          useWindowAsScrollContainer={true}
          onSortEnd={this.onSortEnd}
          onSortStart={this.onSortStart}
          useDragHandle={true}
          lockAxis="y"
          helperClass="cards-list--item-drag-active"
          {...(sortableListOptions || {})}
        />

      </div>
    );
  }

}

Card.Item = CardItem;

export default Card;
