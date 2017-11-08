import React from 'react';
import ReactDOM from 'react-dom';

class ContentEditable extends React.Component {

  static propTypes = {
    html: React.PropTypes.string,
    onChange: React.PropTypes.func,
    onBlur: React.PropTypes.func,
    onEnterPress: React.PropTypes.func,
  };

  componentDidMount() {
    ReactDOM.findDOMNode(this.refs.editable).focus();
  }

  shouldComponentUpdate(nextProps) {
    return nextProps.html !== ReactDOM.findDOMNode(this).innerHTML;
  }

  emitChange() {
    let html = ReactDOM.findDOMNode(this).innerHTML;

    if (this.props.onChange && html !== this.lastHtml) {

      this.props.onChange({
        target: {
          value: html
        }
      });
    }
    this.lastHtml = html;
  }

  onBlur() {
    let html = ReactDOM.findDOMNode(this).innerHTML;
    if (this.props.onBlur) {
      this.props.onBlur({
        target: {
          value: html
        }
      });
    }
  }

  onKeyDown(e) {
    if (e.which === 13) {
      e.preventDefault();
      let html = ReactDOM.findDOMNode(this).innerHTML;
      this.props.onEnterPress({
        target: {
          value: html
        }
      });
    }
  }

  render() {
    const focus = true;
    return (
      <span
        ref="editable"
        autoFocus={focus}
        onKeyDown={this.onKeyDown.bind(this)}
        onInput={this.emitChange.bind(this)}
        onBlur={this.onBlur.bind(this)}
        contentEditable
        // eslint-disable-next-line
        dangerouslySetInnerHTML={{__html: this.props.html}}/>
    );
  }
}

export default ContentEditable;
