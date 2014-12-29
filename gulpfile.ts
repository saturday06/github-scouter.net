declare var require

var gulp = require('gulp')
var tslint = require('gulp-tslint')
var bower = require('gulp-bower')
var mainBowerFiles = require('main-bower-files')
var tsc = require('gulp-tsc')
var tsd = require('gulp-tsd')
var uglify = require('gulp-uglify')
var rename = require('gulp-rename')
var express = require('gulp-express')
var runSequence = require('run-sequence')
var _ = require('lodash')
var webpack = require('gulp-webpack')
var webpackConfig = {
    module: {
        loaders: [
            { test: /\.ts$/, loader: "typescript-loader" }
        ]
    }
}

var src = ["app/assets/**/*.ts"]

gulp.task('webpack', ['tsd'], () => {
    return gulp.src(src)
        .pipe(webpack(_.assign(
            webpackConfig, {
                devtool: "source-map",
                output: {
                    filename: "front.bundle.js"
                }
            })))
        .pipe(gulp.dest('app/assets/javascripts'))
})

gulp.task('webpack-min', ['webpack'], () => {
    return gulp.src('browser/github-scouter.js')
        .pipe(uglify())
        .pipe(rename("github-scouter.min.js"))
        .pipe(gulp.dest('browser/'))
})

gulp.task('tsc', ['tsd'], () => {
    return gulp.src(src)
        .pipe(tsc())
        .pipe(gulp.dest('.'))
})

gulp.task('tslint', ['tsd'], () => {
    return gulp.src(src)
        .pipe(tslint())
        .pipe(tslint.report())
})

gulp.task('test', ['tsd'], () => {
    runSequence('bower', 'tsc', 'tslint', 'webpack')
})

gulp.task('guard', () => {
    gulp.watch(src, ['webpack'])
})

gulp.task('tsd', (done) => {
    tsd({
        command: 'reinstall',
        config: './tsd.json'
    }, done)
})

gulp.task('bower', () => {
    return bower()
        .pipe(gulp.dest('public/bower_components'))
})

gulp.task('main-bower-files', ['bower'], () => {
    return gulp.src(mainBowerFiles({main: '*'}), { base: 'bower_components' })
        .pipe(gulp.dest('public/bower_components'))
});

gulp.task('install', ['bower', 'webpack'], () => {
})

gulp.task('mock-server', () => {
    gulp.src('node_modules/github-scouter/test/mock-server.ts')
        .pipe(tsc({tmpDir: 'tmp'}))
        .pipe(gulp.dest('tmp'))
        .on('end', () => {
            express.run({
                file: 'tmp/mock-server.js'
            })
        })
});

gulp.task('default', ['guard'])
